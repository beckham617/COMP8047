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
  Divider
} from '@mui/material';
import {
  Edit,
  Logout
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const Profile = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

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
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
            <Typography variant="h5" component="h1" sx={{ fontWeight: 'bold' }}>
              Profile
            </Typography>
            <Button
              variant="outlined"
              startIcon={<Logout />}
              onClick={handleLogout}
              sx={{
                color: 'white',
                borderColor: 'rgba(255, 255, 255, 0.5)',
                '&:hover': {
                  borderColor: 'white',
                  backgroundColor: 'rgba(255, 255, 255, 0.1)',
                }
              }}
            >
              Logout
            </Button>
          </Box>
        </Container>
      </Box>

      {/* Profile Content */}
      <Container maxWidth="md" sx={{ py: 3 }}>
        <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
          {/* Avatar and Basic Info */}
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Avatar
              src={user.avatar}
              sx={{
                width: 120,
                height: 120,
                mx: 'auto',
                mb: 2,
                border: '4px solid #1976d2'
              }}
            />
            <Typography variant="h4" component="h2" gutterBottom>
              {user.firstName} {user.lastName}
            </Typography>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              @{user.username}
            </Typography>
          </Box>

          <Divider sx={{ mb: 4 }} />

          {/* Profile Details */}
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Email
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.email}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Username
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.username}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                First Name
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.firstName}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Last Name
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.lastName}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Birth Date
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {formatDate(user.birthYear, user.birthMonth)}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Country
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {user.country}
              </Typography>
            </Grid>

            <Grid item xs={12}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Member Since
              </Typography>
              <Typography variant="body1" sx={{ mb: 3 }}>
                {new Date(user.createdAt).toLocaleDateString('en-US', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric'
                })}
              </Typography>
            </Grid>
          </Grid>

          {/* Edit Button */}
          <Box sx={{ textAlign: 'center', mt: 4 }}>
            <Button
              variant="contained"
              startIcon={<Edit />}
              onClick={() => navigate('/edit-profile')}
              sx={{
                backgroundColor: '#1976d2',
                '&:hover': {
                  backgroundColor: '#1565c0',
                }
              }}
            >
              Edit Profile
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default Profile; 