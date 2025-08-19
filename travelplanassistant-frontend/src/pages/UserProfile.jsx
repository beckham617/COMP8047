import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Paper,
  Avatar,
  Grid,
  Divider,
  IconButton
} from '@mui/material';
import {
  ArrowBack
} from '@mui/icons-material';

const UserProfile = () => {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Load user from localStorage
    const users = JSON.parse(localStorage.getItem('users') || '[]');
    const foundUser = users.find(u => u.id === userId);
    setUser(foundUser);
  }, [userId]);

  const formatDate = (year, month) => {
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return `${monthNames[month - 1]} ${year}`;
  };

  if (!user) {
    return (
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="h6" color="text.secondary">
          User not found
        </Typography>
      </Box>
    );
  }

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
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <IconButton
              onClick={() => navigate(-1)}
              sx={{ color: 'white', mr: 2 }}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h5" component="h1" sx={{ fontWeight: 'bold' }}>
              User Profile
            </Typography>
          </Box>
        </Container>
      </Box>

      {/* Profile Content */}
      <Container maxWidth="md" sx={{ py: 3 }}>
        <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
          {/* Avatar and Basic Info */}
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Avatar
              src={user.profilePicture}
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
        </Paper>
      </Container>
    </Box>
  );
};

export default UserProfile; 