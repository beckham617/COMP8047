import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { travelPlansAPI } from '../services/api';
import { Box, Container, Typography, Grid, Paper, Fab, Tabs, Tab, Tooltip, Snackbar, Alert } from '@mui/material';
import {
  Add
} from '@mui/icons-material';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';
import PlanCard from '../components/PlanCard';

const MyPlans = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState(0);
  // const [plans, setPlans] = useState([]);
  const [currentPlans, setCurrentPlans] = useState([]);
  const [historyPlans, setHistoryPlans] = useState([]);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');

  useEffect(() => {
    loadPlans();
  }, []);

  // Polling for plan updates every 12 seconds to reflect changes in current/history plans
  useEffect(() => {
    const pollPlanUpdates = async () => {
      try {
        const [currentPlansData, historyPlansData] = await Promise.all([
          travelPlansAPI.getCurrentPlans() || [],
          travelPlansAPI.getHistoryPlans() || []
        ]);

        // Update current plans if there are changes
        setCurrentPlans(prevCurrent => {
          if (JSON.stringify(prevCurrent) !== JSON.stringify(currentPlansData)) {
            const newCurrentIds = new Set(currentPlansData.map(p => p.id));
            
            // Check for plans that moved from current to history (completed/cancelled)
            const movedToHistory = prevCurrent.filter(plan => !newCurrentIds.has(plan.id));
            
            if (movedToHistory.length > 0) {
              console.log('MyPlans: Plans moved to history', { 
                moved: movedToHistory.length, 
                planTitles: movedToHistory.map(p => p.title) 
              });
              
              // Show notification for plans moved to history
              if (movedToHistory.length === 1) {
                setSnackbarMessage(`Plan "${movedToHistory[0].title}" moved to history`);
              } else {
                setSnackbarMessage(`${movedToHistory.length} plans moved to history`);
              }
              setSnackbarSeverity('info');
              setSnackbarOpen(true);
            }
            
            console.log('MyPlans: Current plans updated via polling', { 
              previous: prevCurrent.length, 
              current: currentPlansData.length 
            });
            return currentPlansData;
          }
          return prevCurrent;
        });

        // Update history plans if there are changes
        setHistoryPlans(prevHistory => {
          if (JSON.stringify(prevHistory) !== JSON.stringify(historyPlansData)) {
            const prevHistoryIds = new Set(prevHistory.map(p => p.id));
            
            // Check for plans that moved from history to current (reactivated)
            const movedToCurrent = historyPlansData.filter(plan => !prevHistoryIds.has(plan.id));
            
            if (movedToCurrent.length > 0) {
              
              // Show notification for plans moved to current
              if (movedToCurrent.length === 1) {
                setSnackbarMessage(`Plan "${movedToCurrent[0].title}" reactivated`);
              } else {
                setSnackbarMessage(`${movedToCurrent.length} plans reactivated`);
              }
              setSnackbarSeverity('success');
              setSnackbarOpen(true);
            }
            
            return historyPlansData;
          }
          return prevHistory;
        });
      } catch (err) {
        console.error('Failed to poll plan updates:', err);
      }
    };

    // Initial poll after a short delay
    const initialTimeout = setTimeout(pollPlanUpdates, 4000);

    // Set up polling interval
    const pollInterval = setInterval(pollPlanUpdates, 12000); // Poll every 12 seconds

    return () => {
      clearTimeout(initialTimeout);
      clearInterval(pollInterval);
    };
  }, []);

  const loadPlans = useCallback(async ()  => {
    const currentPlans = await travelPlansAPI.getCurrentPlans() || JSON.parse( '[]');
    console.log(JSON.stringify(currentPlans));
    const historyPlans = await travelPlansAPI.getHistoryPlans() || JSON.parse( '[]');
    setCurrentPlans(currentPlans);
    setHistoryPlans(historyPlans);
  }, []);

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const handlePlanClick = (planId) => {
    navigate(`/plan/${planId}`);
  };

  const handleCreatePlan = () => {
    navigate('/create-plan');
  };




  return (
    <PageContainer>
      <PageHeader title="My Plans">
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          sx={{
            '& .MuiTab-root': {
              color: 'rgba(255, 255, 255, 0.6)',
              '&.Mui-selected': {
                color: 'white',
              },
            },
            '& .MuiTabs-indicator': {
              backgroundColor: 'white',
            },
          }}
        >
          <Tab label="Current" />
          <Tab label="History" />
        </Tabs>
      </PageHeader>

      {/* Content */}
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Paper elevation={3} sx={{ 
              p: 4,
              borderRadius: 3,
              position: 'relative',
              backgroundColor: 'transparent',
              zIndex: 1,
            }}>
        {activeTab === 0 ? (
          // Current Tab
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            {currentPlans.length === 0 ? (
              <Box sx={{ textAlign: 'center', py: 8 }}>
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No current plans
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  Create a new plan to get started!
                </Typography>
                <Tooltip title="Create Plan" arrow>
                  <Fab
                    color="primary"
                    aria-label="create plan"
                    onClick={handleCreatePlan}
                    sx={{
                      position: 'fixed',
                      bottom: 120,
                      right: 24,
                      backgroundColor: '#f43d65',
                      '&:hover': {
                        backgroundColor: '#f297ab',
                      }
                    }}
                  >
                    <Add />
                  </Fab>
                </Tooltip>
              </Box>
            ) : (
              <Grid container spacing={3} sx={{ alignItems: 'stretch' }}>
                {currentPlans.map((plan) => (
                  <Grid 
                    item 
                    xs={12} 
                    sm={6} 
                    md={4} 
                    key={plan.id} 
                    sx={{ 
                      display: 'flex',
                      minHeight: '400px',
                      width: '100%',
                      maxWidth: '100%'
                    }}
                  >
                    <PlanCard plan={plan} onClick={() => handlePlanClick(plan.id)} />
                  </Grid>
                ))}
              </Grid>
            )}
          </Box>
        ) : (
          // History Tab
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            {historyPlans.length === 0 ? (
              <Box sx={{ textAlign: 'center', py: 8 }}>
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No history yet
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Your completed and cancelled plans will appear here
                </Typography>
              </Box>
            ) : (
              <Grid container spacing={3} sx={{ alignItems: 'stretch' }}>
                {historyPlans.map((plan) => (
                  <Grid 
                    item 
                    xs={12} 
                    sm={6} 
                    md={4} 
                    key={plan.id} 
                    sx={{ 
                      display: 'flex',
                      minHeight: '400px',
                      width: '100%',
                      maxWidth: '100%'
                    }}
                  >
                    <PlanCard plan={plan} onClick={() => handlePlanClick(plan.id)} />
                  </Grid>
                ))}
              </Grid>
            )}
          </Box>
        )}
        
        </Paper>
      </Container>

      {/* Notification Snackbar */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={4000}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert 
          onClose={() => setSnackbarOpen(false)} 
          severity={snackbarSeverity}
          sx={{ width: '100%' }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </PageContainer>
  );
};

export default MyPlans; 