import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { usersAPI, travelPlansAPI } from '../services/api';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  CardMedia,
  Avatar,
  Chip,
  Grid,
  Paper,
  Fab,
  Tabs,
  Tab,
  Tooltip
} from '@mui/material';
import {
  Add
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import backgroundImage from '../assets/background.avif';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';

const MyPlans = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState(0);
  // const [plans, setPlans] = useState([]);
  const [currentPlans, setCurrentPlans] = useState([]);
  const [historyPlans, setHistoryPlans] = useState([]);

  useEffect(() => {
    loadPlans();
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

  const getUserPlanStatusColor = (status) => {
    switch (status) {
      case 'owned':
        return 'primary';
      case 'applied':
        return 'warning';
      case 'applied_accepted':
        return 'success';
      case 'applied_refused':
        return 'error';
      case 'applied_cancelled':
        return 'error';
      case 'invited':
        return 'info';
      case 'invited_accepted':
        return 'success';
      case 'invited_refused':
        return 'error';
      default:
        return 'default';
    }
  };

  const renderPlanCard = (plan) => {
    // const userPlan = plan.userPlanStatus.find(member => member.userId === user?.id);
    
    return (
      <Card
        key={plan.id}
        sx={{
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          cursor: 'pointer',
          transition: 'transform 0.2s, box-shadow 0.2s',
          '&:hover': {
            transform: 'translateY(-4px)',
            boxShadow: 4,
          }
        }}
        onClick={() => handlePlanClick(plan.id)}
      >
        <CardMedia
          component="img"
          height="200"
          image={(Array.isArray(plan.images) && plan.images.length > 0)
            ? usersAPI.getProfilePicture(plan.images[0])
            : 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'}
          alt={plan.title}
          sx={{ width: '100%', height: 200, objectFit: 'cover', objectPosition: 'center' }}
        />
        <CardContent sx={{ flexGrow: 1, position: 'relative' }}>
          <Typography variant="h6" component="h2" gutterBottom>
            {plan.title}
          </Typography>
          
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {plan.description}
          </Typography>

          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <Avatar
              // src={plan.ownerAvatar}
              src={
                plan.ownerAvatar 
                  ? usersAPI.getProfilePicture(plan.ownerAvatar) 
                  : (plan.ownerAvatar || 'https://via.placeholder.com/150')
              }
              sx={{ width: 32, height: 32, mr: 1 }}
            />
            <Typography variant="body2" color="text.secondary">
              {plan.ownerName}
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Chip
              label={plan.userPlanStatus.replace('_', ' ')}
              color={getStatusColor(plan.userPlanStatus)}
              size="small"
            />
            {/* {userPlan && (
              <Chip
                label={userPlan.userPlanStatus.replace('_', ' ')}
                color={getUserPlanStatusColor(userPlan.userPlanStatus)}
                size="small"
              />
            )} */}
          </Box>
        </CardContent>
      </Card>
    );
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
              <Grid container spacing={3}>
                {currentPlans.map(renderPlanCard)}
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
              <Grid container spacing={3}>
                {historyPlans.map(renderPlanCard)}
              </Grid>
            )}
          </Box>
        )}
        
        </Paper>
      </Container>
    </PageContainer>
  );
};

export default MyPlans; 