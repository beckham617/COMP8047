import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import {
  Box,
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Avatar,
  IconButton,
  Alert,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText
} from '@mui/material';
import {
  PhotoCamera,
  ArrowBack
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const EditProfile = () => {
  const navigate = useNavigate();
  const { user, updateUser } = useAuth();
  const [avatarPreview, setAvatarPreview] = useState(user?.avatar);
  const [error, setError] = useState('');

  const validationSchema = Yup.object({
    username: Yup.string()
      .min(3, 'Username must be at least 3 characters')
      .max(20, 'Username must be at most 20 characters')
      .required('Username is required'),
    email: Yup.string()
      .email('Invalid email address')
      .required('Email is required'),
    firstName: Yup.string()
      .min(2, 'First name must be at least 2 characters')
      .required('First name is required'),
    lastName: Yup.string()
      .min(2, 'Last name must be at least 2 characters')
      .required('Last name is required'),
    birthYear: Yup.number()
      .min(1900, 'Invalid birth year')
      .max(new Date().getFullYear(), 'Birth year cannot be in the future')
      .required('Birth year is required'),
    birthMonth: Yup.number()
      .min(1, 'Invalid month')
      .max(12, 'Invalid month')
      .required('Birth month is required'),
    country: Yup.string()
      .required('Country is required'),
    avatar: Yup.mixed()
      .test('fileSize', 'File size is too large', (value) => {
        if (!value) return true;
        return value.size <= 5000000; // 5MB
      })
      .test('fileType', 'Unsupported file type', (value) => {
        if (!value) return true;
        return ['image/jpeg', 'image/png', 'image/gif'].includes(value.type);
      })
  });

  const formik = useFormik({
    initialValues: {
      username: user?.username || '',
      email: user?.email || '',
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      birthYear: user?.birthYear || '',
      birthMonth: user?.birthMonth || '',
      country: user?.country || ''
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        setError('');
        
        const updatedUserData = {
          ...user,
          ...values,
          avatar: avatarPreview || user.avatar
        };

        // Update user in localStorage
        const users = JSON.parse(localStorage.getItem('users') || '[]');
        const updatedUsers = users.map(u => u.id === user.id ? updatedUserData : u);
        localStorage.setItem('users', JSON.stringify(updatedUsers));

        // Update current user
        updateUser(updatedUserData);
        navigate('/profile');
      } catch (err) {
        setError('Failed to update profile. Please try again.');
      }
    }
  });

  const handleAvatarChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      formik.setFieldValue('avatar', file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setAvatarPreview(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const countries = [
    'United States', 'Canada', 'United Kingdom', 'Australia', 'Germany',
    'France', 'Japan', 'China', 'India', 'Brazil', 'Mexico', 'Italy',
    'Spain', 'Netherlands', 'Sweden', 'Norway', 'Denmark', 'Finland',
    'Switzerland', 'Austria', 'Belgium', 'Portugal', 'Greece', 'Poland',
    'Czech Republic', 'Hungary', 'Romania', 'Bulgaria', 'Croatia',
    'Slovenia', 'Slovakia', 'Estonia', 'Latvia', 'Lithuania'
  ];

  if (!user) {
    return null;
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
              onClick={() => navigate('/profile')}
              sx={{ color: 'white', mr: 2 }}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h5" component="h1" sx={{ fontWeight: 'bold' }}>
              Edit Profile
            </Typography>
          </Box>
        </Container>
      </Box>

      {/* Form */}
      <Container maxWidth="md" sx={{ py: 3 }}>
        <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={formik.handleSubmit}>
            <Grid container spacing={3}>
              {/* Avatar Upload */}
              <Grid item xs={12} sx={{ textAlign: 'center' }}>
                <Box sx={{ position: 'relative', display: 'inline-block' }}>
                  <Avatar
                    src={avatarPreview}
                    sx={{
                      width: 100,
                      height: 100,
                      border: '3px solid #1976d2',
                      mb: 2
                    }}
                  />
                  <IconButton
                    color="primary"
                    aria-label="upload picture"
                    component="label"
                    sx={{
                      position: 'absolute',
                      bottom: 0,
                      right: 0,
                      backgroundColor: '#1976d2',
                      color: 'white',
                      '&:hover': {
                        backgroundColor: '#1565c0',
                      }
                    }}
                  >
                    <input
                      hidden
                      accept="image/*"
                      type="file"
                      onChange={handleAvatarChange}
                    />
                    <PhotoCamera />
                  </IconButton>
                </Box>
                {formik.touched.avatar && formik.errors.avatar && (
                  <FormHelperText error sx={{ mt: 1 }}>
                    {formik.errors.avatar}
                  </FormHelperText>
                )}
              </Grid>

              {/* Username */}
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  name="username"
                  label="Username"
                  value={formik.values.username}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.username && Boolean(formik.errors.username)}
                  helperText={formik.touched.username && formik.errors.username}
                />
              </Grid>

              {/* Email */}
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  name="email"
                  label="Email"
                  type="email"
                  value={formik.values.email}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.email && Boolean(formik.errors.email)}
                  helperText={formik.touched.email && formik.errors.email}
                />
              </Grid>

              {/* First Name */}
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  name="firstName"
                  label="First Name"
                  value={formik.values.firstName}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.firstName && Boolean(formik.errors.firstName)}
                  helperText={formik.touched.firstName && formik.errors.firstName}
                />
              </Grid>

              {/* Last Name */}
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  name="lastName"
                  label="Last Name"
                  value={formik.values.lastName}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.lastName && Boolean(formik.errors.lastName)}
                  helperText={formik.touched.lastName && formik.errors.lastName}
                />
              </Grid>

              {/* Birth Year */}
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  name="birthYear"
                  label="Birth Year"
                  type="number"
                  value={formik.values.birthYear}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.birthYear && Boolean(formik.errors.birthYear)}
                  helperText={formik.touched.birthYear && formik.errors.birthYear}
                />
              </Grid>

              {/* Birth Month */}
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth error={formik.touched.birthMonth && Boolean(formik.errors.birthMonth)}>
                  <InputLabel>Birth Month</InputLabel>
                  <Select
                    name="birthMonth"
                    value={formik.values.birthMonth}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    label="Birth Month"
                  >
                    {Array.from({ length: 12 }, (_, i) => i + 1).map((month) => (
                      <MenuItem key={month} value={month}>
                        {new Date(2000, month - 1).toLocaleString('default', { month: 'long' })}
                      </MenuItem>
                    ))}
                  </Select>
                  {formik.touched.birthMonth && formik.errors.birthMonth && (
                    <FormHelperText>{formik.errors.birthMonth}</FormHelperText>
                  )}
                </FormControl>
              </Grid>

              {/* Country */}
              <Grid item xs={12}>
                <FormControl fullWidth error={formik.touched.country && Boolean(formik.errors.country)}>
                  <InputLabel>Country</InputLabel>
                  <Select
                    name="country"
                    value={formik.values.country}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    label="Country"
                  >
                    {countries.map((country) => (
                      <MenuItem key={country} value={country}>
                        {country}
                      </MenuItem>
                    ))}
                  </Select>
                  {formik.touched.country && formik.errors.country && (
                    <FormHelperText>{formik.errors.country}</FormHelperText>
                  )}
                </FormControl>
              </Grid>

              {/* Submit Button */}
              <Grid item xs={12}>
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={formik.isSubmitting}
                  sx={{
                    py: 1.5,
                    fontSize: '1.1rem',
                    fontWeight: 'bold',
                    backgroundColor: '#1976d2',
                    '&:hover': {
                      backgroundColor: '#1565c0',
                    }
                  }}
                >
                  {formik.isSubmitting ? 'Saving Changes...' : 'Save Changes'}
                </Button>
              </Grid>
            </Grid>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default EditProfile; 