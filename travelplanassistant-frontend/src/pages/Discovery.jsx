import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Container, Typography, Grid, Fab, TextField, InputAdornment, Paper, Snackbar, Alert } from '@mui/material';
import {
  Add,
  Search
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';
import { travelPlansAPI } from '../services/api';
import PlanCard from '../components/PlanCard';

const Discovery = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [plans, setPlans] = useState([]);
  const [filteredPlans, setFilteredPlans] = useState([]);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');

  useEffect(() => {
    const loadDiscovery = async () => {
      try {
        const results = await travelPlansAPI.getDiscoveryPlans();
        setPlans(Array.isArray(results) ? results : []);
      } catch (e) {
        console.error('Failed to load discovery plans:', e);
        setPlans([]);
      }
    };
    loadDiscovery();
  }, []);

  // Polling for new travel plans every 15 seconds
  useEffect(() => {
    const pollNewPlans = async () => {
      try {
        const results = await travelPlansAPI.getDiscoveryPlans();
        const newPlans = Array.isArray(results) ? results : [];
        
        setPlans(prevPlans => {
          // Only update if there are actual changes to avoid unnecessary re-renders
          if (JSON.stringify(prevPlans) !== JSON.stringify(newPlans)) {
            const prevPlanIds = new Set(prevPlans.map(p => p.id));
            const newPlanIds = new Set(newPlans.map(p => p.id));
            
            // Check for new plans
            const addedPlans = newPlans.filter(plan => !prevPlanIds.has(plan.id));
            const removedPlans = prevPlans.filter(plan => !newPlanIds.has(plan.id));
            
            if (addedPlans.length > 0) {
              console.log('Discovery: New plans detected', { 
                added: addedPlans.length, 
                planTitles: addedPlans.map(p => p.title) 
              });
              
              // Show notification for new plans
              if (addedPlans.length === 1) {
                setSnackbarMessage(`New travel plan: "${addedPlans[0].title}"`);
              } else {
                setSnackbarMessage(`${addedPlans.length} new travel plans available!`);
              }
              setSnackbarSeverity('success');
              setSnackbarOpen(true);
            }
            
            if (removedPlans.length > 0) {
              console.log('Discovery: Plans removed', { 
                removed: removedPlans.length, 
                planTitles: removedPlans.map(p => p.title) 
              });
            }
            
            return newPlans;
          }
          return prevPlans;
        });
      } catch (e) {
        console.error('Failed to poll discovery plans:', e);
      }
    };

    // Initial poll after a short delay
    const initialTimeout = setTimeout(pollNewPlans, 5000);

    // Set up polling interval
    const pollInterval = setInterval(pollNewPlans, 15000); // Poll every 15 seconds

    return () => {
      clearTimeout(initialTimeout);
      clearInterval(pollInterval);
    };
  }, []);

  useEffect(() => {
    // Filter plans based on search term and user eligibility
    const filtered = plans.filter(plan => {
      const matchesSearch = plan.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          plan.description.toLowerCase().includes(searchTerm.toLowerCase()) || 
                          (plan.ownerName && plan.ownerName.toLowerCase().includes(searchTerm.toLowerCase()));
      
      const statusLower = (plan.planStatus || plan.status || '').toString().toLowerCase();
      const visibilityUpper = (plan.visibility || plan.planType || '').toString().toUpperCase();
      const members = Array.isArray(plan.members) ? plan.members : [];
      
      // Count accepted members (OWNED, APPLIED_ACCEPTED, INVITED_ACCEPTED)
      const acceptedMembers = members.filter(member => 
        ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'].includes(
          (member.userPlanStatus || '').toString().toUpperCase()
        )
      );
      const currentMemberCount = acceptedMembers.length;
      const maxMembers = plan.maxMembers || members.length;
      
      const isEligible = statusLower === 'new' && 
                        visibilityUpper === 'PUBLIC' &&
                        !members.some(member => member.userId === user?.id) &&
                        currentMemberCount < maxMembers; // Don't show plans that are full
      
      return matchesSearch && isEligible;
    });
    
    setFilteredPlans(filtered);
  }, [plans, searchTerm, user]);

  const handlePlanClick = (planId) => {
    navigate(`/plan/${planId}`, { state: { from: 'discovery' } });
  };

  const handleCreatePlan = () => {
    navigate('/create-plan');
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'new':
        return 'success';
      case 'in_progress':
        return 'warning';
      case 'completed':
        return 'info';
      case 'cancelled':
        return 'error';
      default:
        return 'default';
    }
  };

  return (
    <PageContainer>
      <PageHeader title="Discovery">
        <TextField
          fullWidth
          placeholder="Search plans..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search sx={{ color: 'rgba(255, 255, 255, 0.7)' }} />
              </InputAdornment>
            ),
            sx: {
              backgroundColor: 'rgba(255, 255, 255, 0.1)',
              borderRadius: 2,
              '& .MuiOutlinedInput-notchedOutline': {
                borderColor: 'rgba(255, 255, 255, 0.3)',
              },
              '&:hover .MuiOutlinedInput-notchedOutline': {
                borderColor: 'rgba(255, 255, 255, 0.5)',
              },
              '& .MuiInputBase-input': {
                color: 'white',
                '&::placeholder': {
                  color: 'rgba(255, 255, 255, 0.7)',
                  opacity: 1,
                },
              },
            }
          }}
        />
      </PageHeader>

      {/* Plans Grid */}
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Paper elevation={3} sx={{ 
              p: 4,
              borderRadius: 3,
              position: 'relative',
              backgroundColor: 'transparent',
              zIndex: 1,
            }}>
        {filteredPlans.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              {searchTerm ? 'No plans found matching your search' : 'No plans available'}
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 7 }}>
              {searchTerm ? 'Try adjusting your search terms' : 'Be the first to create a plan!'}
            </Typography>
          </Box>
        ) : (
          <Grid container spacing={3} sx={{ alignItems: 'stretch' }}>
            {filteredPlans.map((plan) => (
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

export default Discovery; 