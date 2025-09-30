import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  IconButton,
  Typography,
  Avatar,
  Box,
  Grid,
  Divider,
  CircularProgress,
  Alert
} from '@mui/material';
import { Close } from '@mui/icons-material';
import { usersAPI } from '../services/api';

const UserInfoDialog = ({ open, onClose, userId }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const resolveProfilePictureUrl = (value) => {
    if (!value) return undefined;
    try {
      const str = String(value);
      if (str.startsWith('http://') || str.startsWith('https://') || str.startsWith('data:')) {
        return str;
      }
      return usersAPI.getProfilePicture(str);
    } catch {
      return undefined;
    }
  };

  useEffect(() => {
    const fetchUser = async () => {
      if (!userId || !open) return;
      
      setLoading(true);
      setError('');
      try {
        const userData = await usersAPI.getProfile(userId);
        setUser(userData);
      } catch (err) {
        console.error('Failed to fetch user profile:', err);
        setError('Failed to load user information');
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [userId, open]);

  const handleClose = () => {
    onClose();
    // Reset state when closing
    setUser(null);
    setError('');
  };

  const formatDate = (year, month) => {
    if (!year || !month) return 'Not specified';
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return `${monthNames[month - 1]} ${year}`;
  };

  const calculateAge = (year, month) => {
    if (!year || !month) return null;
    const today = new Date();
    const birthDate = new Date(year, month - 1, 1);
    const age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    return monthDiff < 0 ? age - 1 : age;
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', pb: 1 }}>
        <Typography variant="h6">User Profile</Typography>
        <IconButton onClick={handleClose} size="small">
          <Close />
        </IconButton>
      </DialogTitle>
      
      <DialogContent>
        {loading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        )}
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        
        {user && !loading && (
          <Box>
            {/* User Header */}
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
              <Avatar
                src={
                  user.avatar
                    ? usersAPI.getProfilePicture(user.avatar)
                    : resolveProfilePictureUrl(user.profilePicture) || 'https://via.placeholder.com/150'
                }
                sx={{ width: 80, height: 80, mr: 2 }}
              />
              <Box>
                <Typography variant="h5" component="h2" gutterBottom>
                  {user.fullName || [user.firstName, user.lastName].filter(Boolean).join(' ') || 'Unknown User'}
                </Typography>
                {user.username && (
                  <Typography variant="body1" color="text.secondary" gutterBottom>
                    @{user.username}
                  </Typography>
                )}
                {user.email && (
                  <Typography variant="body2" color="text.secondary">
                    {user.email}
                  </Typography>
                )}
              </Box>
            </Box>

            <Divider sx={{ mb: 3 }} />

            {/* Personal Information Section */}
            <Box sx={{ mb: 4 }}>
              <Typography 
                variant="h6" 
                sx={{ 
                  color: 'primary.main', 
                  fontWeight: 600,
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis'
                }}
              >
                Personal Information
              </Typography>
              <Box sx={{ 
                p: 1, 
                backgroundColor: 'rgba(0, 0, 0, 0.02)', 
                borderRadius: 2,
                border: '1px solid rgba(0, 0, 0, 0.1)'
              }}>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Gender
                    </Typography>
                    <Typography variant="body1">
                      {user.gender ? user.gender.charAt(0).toUpperCase() + user.gender.slice(1).toLowerCase() : 'Not specified'}
                    </Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Age
                    </Typography>
                    <Typography variant="body1">
                      {user.birthYear && user.birthMonth 
                        ? `${calculateAge(user.birthYear, user.birthMonth)} years old`
                        : 'Not specified'
                      }
                    </Typography>
                  </Grid>
                  <Grid item xs={12}>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Birth Date
                    </Typography>
                    <Typography variant="body1">
                      {formatDate(user.birthYear, user.birthMonth)}
                    </Typography>
                  </Grid>
                </Grid>
              </Box>
            </Box>

            {/* Location Information Section */}
            <Box sx={{ mb: 4 }}>
              <Typography 
                variant="h6" 
                sx={{ 
                  color: 'primary.main', 
                  fontWeight: 600,
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis'
                }}
              >
                Location
              </Typography>
              <Box sx={{ 
                p: 1, 
                backgroundColor: 'rgba(0, 0, 0, 0.02)', 
                borderRadius: 2,
                border: '1px solid rgba(0, 0, 0, 0.1)'
              }}>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      Country
                    </Typography>
                    <Typography variant="body1">
                      {user.country || 'Not specified'}
                    </Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                      City
                    </Typography>
                    <Typography variant="body1">
                      {user.city || 'Not specified'}
                    </Typography>
                  </Grid>
                </Grid>
              </Box>
            </Box>

            {/* Additional Information Section */}
            {user.language && (
              <Box sx={{ mb: 4 }}>
                <Typography 
                  variant="h6" 
                  sx={{ 
                    color: 'primary.main', 
                    fontWeight: 600,
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis'
                  }}
                >
                  Additional Information
                </Typography>
                <Box sx={{ 
                  p: 1, 
                  backgroundColor: 'rgba(0, 0, 0, 0.02)', 
                  borderRadius: 2,
                  border: '1px solid rgba(0, 0, 0, 0.1)'
                }}>
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                        Language
                      </Typography>
                      <Typography variant="body1">
                        {user.language}
                      </Typography>
                    </Grid>
                  </Grid>
                </Box>
              </Box>
            )}
          </Box>
        )}
      </DialogContent>
    </Dialog>
  );
};

export default UserInfoDialog;
