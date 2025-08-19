import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Avatar,
  Paper,
  Button,
  Grid,
  Divider,
  Fab,
  Tabs,
  Tab,
  Tooltip
} from '@mui/material';
import {
  Edit,
  Logout
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { usersAPI } from '../services/api';
import { useEffect, useState } from 'react';
import backgroundImage from '../assets/background.avif';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';

const Profile = () => {
  const navigate = useNavigate();
  const { user, logout, reloadUser } = useAuth();
  const [hasLoaded, setHasLoaded] = useState(false);
  console.log("user" + user);
  useEffect(() => {
    console.log('user:', JSON.stringify(user));
    // Only reload once when component mounts and user is not loaded
    if (!user && reloadUser && !hasLoaded) {
      setHasLoaded(true);
      reloadUser();
    }
  }, [user, reloadUser, hasLoaded]);

  // Debug: Log user data to see what properties are available
  useEffect(() => {
    if (user) {
      console.log('User data:', user);
      console.log('Profile picture path:', user.profilePicture);
    }
  }, [user]);

  if (!user) {
    return null;
  }

  const formatDate = (year, month) => {
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return `${monthNames[month - 1]} ${year}`;
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <PageContainer>
      <PageHeader title="Profile" onLogout={handleLogout} >
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          You can view and edit your profile here
        </Typography>
      </PageHeader>

      {/* Profile Content */}
      <Container maxWidth="md" sx={{ py: 4 }}>
        <Paper elevation={3} sx={{ 
            p: 4,
            borderRadius: 3,
            position: 'relative',
            backgroundColor: 'transparent',
            zIndex: 1,
          }}>
          {/* Avatar and Basic Info */}
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Avatar
              src={
                user.profilePicture 
                  ? usersAPI.getProfilePicture(user.profilePicture) 
                  : (user.profilePicture || 'https://via.placeholder.com/150')
              }
              sx={{
                width: 120,
                height: 120,
                mx: 'auto',
                mb: 2,
                border: '4px solid #f43d65'
              }}
              onError={(e) => {
                console.error('Failed to load profile picture:', e.target.src);
                // Fallback to default profilePicture
                e.target.src = user.profilePicture || 'https://via.placeholder.com/150';
              }}
            />
            <Typography variant="h4" component="h2" gutterBottom>
              {user.firstName} {user.lastName}
            </Typography>
          </Box>

          <Divider sx={{ mb: 4 }} />

          {/* Profile Details */}
          <Grid container spacing={4}>
            <Grid item xs={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                First Name
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.firstName}
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Last Name
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.lastName}
              </Typography>
            </Grid>
          </Grid>
          <Grid container spacing={4}>
            <Grid item xs={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Email
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.email}
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Phone Number
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.phoneNumber || 'Not specified'}
              </Typography>
            </Grid>
          </Grid>
          <Grid container spacing={4}>
            <Grid item xs={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Gender
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.gender ? user.gender.charAt(0).toUpperCase() + user.gender.slice(1).toLowerCase() : 'Not specified'}
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Birth Year and Month
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.birthYear && user.birthMonth 
                  ? formatDate(user.birthYear, user.birthMonth)
                  : 'Not specified'
                }
              </Typography>
            </Grid>
          </Grid>
          <Grid container spacing={4}>
            <Grid item xs={4}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Country
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.country || 'Not specified'}
              </Typography>
            </Grid>
            <Grid item xs={4}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                City
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.city || 'Not specified'}
              </Typography>
            </Grid>
            <Grid item xs={4}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Language
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.language || 'Not specified'}
              </Typography>
            </Grid>
          </Grid>
          <Grid container spacing={4}>
            <Grid item xs={12}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Bio
              </Typography>
              <Typography variant="body1"   
              sx={{ 
                mb: 3,
                whiteSpace: 'pre-wrap', // Preserves line breaks and wraps
                wordBreak: 'break-word' // Breaks long words if needed
              }}>
                {user.bio || 'No bio available'}
              </Typography>
            </Grid>
          </Grid>
          <Grid container spacing={4}>
            <Grid item xs={12}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Member Since
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.createdAt ? new Date(user.createdAt).toLocaleDateString('en-US', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric'
                }) : ''}
              </Typography>
            </Grid>
          </Grid>
        </Paper>
      </Container>

      {/* Floating Edit Profile Button */}
      <Tooltip title="Edit Profile" arrow>
        <Fab
          color="primary"
          aria-label="edit profile"
          onClick={() => navigate('/edit-profile')}
          sx={{
            position: 'fixed',
            bottom: 120,
            right: 24,
            backgroundColor: '#f43d65',
            '&:hover': {
              backgroundColor: '#f297ab',
            },
            zIndex: 1000
          }}
        >
          <Edit />
        </Fab>
      </Tooltip>
    </PageContainer>
  );
};

export default Profile; 