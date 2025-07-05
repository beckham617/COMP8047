import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Tabs,
  Tab,
  Card,
  CardContent,
  CardMedia,
  Avatar,
  Chip,
  Button,
  Grid
} from '@mui/material';
import {
  Add
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const MyPlans = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState(0);
  const [plans, setPlans] = useState([]);
  const [currentPlans, setCurrentPlans] = useState([]);
  const [historyPlans, setHistoryPlans] = useState([]);

  useEffect(() => {
    // Load plans from localStorage
    const storedPlans = JSON.parse(localStorage.getItem('plans') || '[]');
    setPlans(storedPlans);
  }, []);

  useEffect(() => {
    if (!user) return;

    // Filter current plans (new or in_progress with active user involvement)
    const current = plans.filter(plan => {
      const userPlan = plan.members.find(member => member.userId === user.id);
      if (!userPlan) return false;
      
      // Only show in Current if plan is active AND user has an active status
      const isActivePlan = ['new', 'in_progress'].includes(plan.planStatus);
      const isActiveUserStatus = ['owned', 'applied', 'applied_accepted', 'invited', 'invited_accepted'].includes(userPlan.userPlanStatus);
      
      return isActivePlan && isActiveUserStatus;
    });

    // Filter history plans (completed, cancelled, or inactive user statuses)
    const history = plans.filter(plan => {
      const userPlan = plan.members.find(member => member.userId === user.id);
      if (!userPlan) return false;
      
      // Show in History if plan is completed/cancelled OR user has inactive status
      const isCompletedPlan = plan.planStatus === 'completed' || plan.planStatus === 'cancelled';
      const isInactiveUserStatus = ['applied_cancelled', 'applied_refused', 'invited_refused'].includes(userPlan.userPlanStatus);
      
      return isCompletedPlan || isInactiveUserStatus;
    });

    setCurrentPlans(current);
    setHistoryPlans(history);
  }, [plans, user]);

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
    const userPlan = plan.members.find(member => member.userId === user?.id);
    
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
          image={plan.coverImage || 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'}
          alt={plan.title}
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
              src={plan.owner.avatar}
              sx={{ width: 32, height: 32, mr: 1 }}
            />
            <Typography variant="body2" color="text.secondary">
              {plan.owner.firstName} {plan.owner.lastName}
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Chip
              label={plan.planStatus.replace('_', ' ')}
              color={getStatusColor(plan.planStatus)}
              size="small"
            />
            {userPlan && (
              <Chip
                label={userPlan.userPlanStatus.replace('_', ' ')}
                color={getUserPlanStatusColor(userPlan.userPlanStatus)}
                size="small"
              />
            )}
          </Box>
        </CardContent>
      </Card>
    );
  };

  return (
    <Box sx={{ pb: 8 }}>
      {/* Header */}
      <Box
        sx={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          color: 'white',
          py: 3,
          px: 2
        }}
      >
        <Container maxWidth="lg">
          <Typography variant="h5" component="h1" sx={{ fontWeight: 'bold', mb: 2 }}>
            My Plans
          </Typography>
          
          <Tabs
            value={activeTab}
            onChange={handleTabChange}
            sx={{
              '& .MuiTab-root': {
                color: 'rgba(255, 255, 255, 0.7)',
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
        </Container>
      </Box>

      {/* Content */}
      <Container maxWidth="lg" sx={{ py: 3 }}>
        {activeTab === 0 ? (
          // Current Tab
          <Box>
            {currentPlans.length === 0 ? (
              <Box sx={{ textAlign: 'center', py: 8 }}>
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No current plans
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  Create a new plan to get started!
                </Typography>
                <Button
                  variant="contained"
                  startIcon={<Add />}
                  onClick={handleCreatePlan}
                  sx={{
                    backgroundColor: '#1976d2',
                    '&:hover': {
                      backgroundColor: '#1565c0',
                    }
                  }}
                >
                  NEW
                </Button>
              </Box>
            ) : (
              <Grid container spacing={3}>
                {currentPlans.map(renderPlanCard)}
              </Grid>
            )}
          </Box>
        ) : (
          // History Tab
          <Box>
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
      </Container>
    </Box>
  );
};

export default MyPlans; 