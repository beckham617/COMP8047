import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
  Fab,
  TextField,
  InputAdornment,
  Paper
} from '@mui/material';
import {
  Add,
  Search
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';

const Discovery = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [plans, setPlans] = useState([]);
  const [filteredPlans, setFilteredPlans] = useState([]);

  useEffect(() => {
    // Load plans from localStorage
    const storedPlans = JSON.parse(localStorage.getItem('plans') || '[]');
    setPlans(storedPlans);
  }, []);

  useEffect(() => {
    // Filter plans based on search term and user eligibility
    const filtered = plans.filter(plan => {
      const matchesSearch = plan.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          plan.description.toLowerCase().includes(searchTerm.toLowerCase());
      
      const isEligible = plan.planStatus === 'new' && 
                        plan.visibility === 'PUBLIC' &&
                        !plan.members.some(member => member.userId === user?.id);
      
      return matchesSearch && isEligible;
    });
    
    setFilteredPlans(filtered);
  }, [plans, searchTerm, user]);

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
      <Container maxWidth="lg" sx={{ py: 4 }}>
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
            <Typography variant="body2" color="text.secondary">
              {searchTerm ? 'Try adjusting your search terms' : 'Be the first to create a plan!'}
            </Typography>
          </Box>
        ) : (
          <Grid container spacing={3}>
            {filteredPlans.map((plan) => (
              <Grid item xs={12} sm={6} md={4} key={plan.id}>
                <Card
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
                        src={plan.owner.profilePicture}
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
                      <Typography variant="body2" color="text.secondary">
                        {plan.members.length} members
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
        </Paper>
      </Container>
    </PageContainer>
  );
};

export default Discovery; 