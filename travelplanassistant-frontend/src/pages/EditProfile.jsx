import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { useLayoutEffect, useRef } from 'react';
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
  FormHelperText,
  Fab,
  Tooltip
} from '@mui/material';
import {
  PhotoCamera,
  Save
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { usersAPI } from '../services/api';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';

const EditProfile = () => {
  const navigate = useNavigate();
  const { user, updateUser } = useAuth();
  const [profilePicture, setProfilePicture] = useState(
    user?.profilePicture ? usersAPI.getProfilePicture(user.profilePicture) : user?.profilePicture
  );
  const [error, setError] = useState('');

  // Scroll to top when component mounts
  useLayoutEffect(() => {
    // Use requestAnimationFrame for better timing
    requestAnimationFrame(() => {
      setTimeout(() => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        document.documentElement.scrollTop = 0;
        document.body.scrollTop = 0;
      }, 100);
    });
  }, []);

  const validationSchema = Yup.object({
    email: Yup.string()
      .email('Invalid email address')
      .required('Email is required'),
    firstName: Yup.string()
      .min(2, 'First name must be at least 2 characters')
      .required('First name is required'),
    lastName: Yup.string()
      .min(2, 'Last name must be at least 2 characters')
      .required('Last name is required'),
    gender: Yup.string()
      .oneOf(['MALE', 'FEMALE', 'OTHER'], 'Please select a valid gender')
      .required('Gender is required'),
    birthYear: Yup.number()
      .min(1900, 'Invalid birth year')
      .max(new Date().getFullYear(), 'Birth year cannot be in the future')
      .required('Birth year is required'),
    birthMonth: Yup.number()
      .min(1, 'Invalid month')
      .max(12, 'Invalid month')
      .required('Birth month is required'),
    phoneNumber: Yup.string()
      .required('Phone number is required'),
    language: Yup.string()
      .required('Language is required'),
    country: Yup.string()
      .required('Country is required'),
    city: Yup.string()
      .required('City is required'),
    profilePicture: Yup.mixed()
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
      id: user?.id || '',
      email: user?.email || '',
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      gender: user?.gender || '',
      birthYear: user?.birthYear || '',
      birthMonth: user?.birthMonth || '',
      phoneNumber: user?.phoneNumber || '',
      language: user?.language || '',
      country: user?.country || '',
      city: user?.city || '',
      bio: user?.bio || ''
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        setError('');
        
        const updatedUserData = {
          ...user,
          ...values
        };
        // Upload profile picture first if selected
        if (values.profilePicture instanceof File) {
          try {
            const uploadResponse = await usersAPI.uploadProfilePicture(values.profilePicture);
            updatedUserData.profilePicture = uploadResponse.filePath;
          } catch (uploadError) {
            setError('Failed to upload profile picture: ' + uploadError.message);
            return;
          }
        }

        // Update user profile via API
        await usersAPI.updateProfile(updatedUserData);

        // Update current user
        updateUser(updatedUserData);
        navigate('/profile');
      } catch (err) {
        setError(err.message || 'Failed to update profile. Please try again.');
      }
    }
  });

  const handleAvatarChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      formik.setFieldValue('profilePicture', file);
      // Create a preview URL for display
      const reader = new FileReader();
      reader.onload = (e) => {
        setProfilePicture(e.target.result);
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

  const handleBack = () => {
    navigate('/profile');
  };

  return (
    <PageContainer>
      <PageHeader title="Edit Profile" onBack={handleBack} >
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          Please save after editing
        </Typography>
      </PageHeader>

      <Container maxWidth="sm" sx={{ position: 'relative', zIndex: 1 }}>
        <Paper
          elevation={8}
          sx={{
            p: 4,
            borderRadius: 3,
            position: 'relative',
            backgroundColor: 'transparent',
            zIndex: 1,
          }}
        >
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          <Box component="form" onSubmit={formik.handleSubmit}>
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3 }}>
              {/* Avatar Upload */}
              <Box sx={{ position: 'relative', display: 'inline-block', mb: 2 }}>
                <Avatar
                  src={profilePicture}
                  sx={{
                    width: 100,
                    height: 100,
                    border: '3px solid #f43d65',
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
                    backgroundColor: '#f43d65',
                    color: 'white',
                    '&:hover': {
                      backgroundColor: '#f297ab',
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
              {formik.touched.profilePicture && formik.errors.profilePicture && (
                <FormHelperText error sx={{ mt: 1 }}>
                  {formik.errors.profilePicture}
                </FormHelperText>
              )}

              {/* Email */}
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
                InputProps={{
                  readOnly: true,
                }}
                sx={{
                  '& .MuiInputBase-root': {
                    backgroundColor: 'rgba(0, 0, 0, 0.04)',
                  },
                  '& .MuiInputBase-input': {
                    color: 'text.secondary',
                  }
                }}
              />

              {/* First Name */}
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

              {/* Last Name */}
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

              {/* Gender */}
              <FormControl fullWidth error={formik.touched.gender && Boolean(formik.errors.gender)}>
                <InputLabel>Gender</InputLabel>
                <Select
                  name="gender"
                  value={formik.values.gender}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  label="Gender"
                >
                  <MenuItem value="MALE">Male</MenuItem>
                  <MenuItem value="FEMALE">Female</MenuItem>
                  <MenuItem value="OTHER">Other</MenuItem>
                </Select>
                {formik.touched.gender && formik.errors.gender && (
                  <FormHelperText>{formik.errors.gender}</FormHelperText>
                )}
              </FormControl>

              {/* Birth Year */}
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

              {/* Birth Month */}
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
              {/* Phone Number */}
              <TextField
                fullWidth
                name="phoneNumber"
                label="Phone Number"
                value={formik.values.phoneNumber}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                error={formik.touched.phoneNumber && Boolean(formik.errors.phoneNumber)}
                helperText={formik.touched.phoneNumber && formik.errors.phoneNumber}
              />
              {/* Language */}
              <FormControl fullWidth error={formik.touched.language && Boolean(formik.errors.language)}>
                <InputLabel>Language</InputLabel>
                <Select
                  name="language"
                  value={formik.values.language}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  label="Language"
                >
                  <MenuItem value="English">English</MenuItem>
                  <MenuItem value="Spanish">Spanish</MenuItem>
                  <MenuItem value="French">French</MenuItem>
                  <MenuItem value="German">German</MenuItem>
                  <MenuItem value="Italian">Italian</MenuItem>
                  <MenuItem value="Portuguese">Portuguese</MenuItem>
                  <MenuItem value="Russian">Russian</MenuItem>
                  <MenuItem value="Chinese">Chinese</MenuItem>
                  <MenuItem value="Japanese">Japanese</MenuItem>
                  <MenuItem value="Korean">Korean</MenuItem>
                  <MenuItem value="Arabic">Arabic</MenuItem>
                  <MenuItem value="Hindi">Hindi</MenuItem>
                  <MenuItem value="Dutch">Dutch</MenuItem>
                  <MenuItem value="Swedish">Swedish</MenuItem>
                  <MenuItem value="Norwegian">Norwegian</MenuItem>
                  <MenuItem value="Danish">Danish</MenuItem>
                  <MenuItem value="Finnish">Finnish</MenuItem>
                  <MenuItem value="Polish">Polish</MenuItem>
                  <MenuItem value="Czech">Czech</MenuItem>
                  <MenuItem value="Hungarian">Hungarian</MenuItem>
                  <MenuItem value="Romanian">Romanian</MenuItem>
                  <MenuItem value="Bulgarian">Bulgarian</MenuItem>
                  <MenuItem value="Greek">Greek</MenuItem>
                  <MenuItem value="Turkish">Turkish</MenuItem>
                </Select>
                {formik.touched.language && formik.errors.language && (
                  <FormHelperText>{formik.errors.language}</FormHelperText>
                )}
              </FormControl>
              {/* Country */}
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

              {/* City */}
              <TextField
                fullWidth
                name="city"
                label="City"
                value={formik.values.city}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                error={formik.touched.city && Boolean(formik.errors.city)}
                helperText={formik.touched.city && formik.errors.city}
              />
              {/* Bio */}
              <TextField
                fullWidth
                name="bio"
                label="Bio (max 500 characters)"
                multiline
                rows={4}
                value={formik.values.bio}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                error={formik.touched.bio && Boolean(formik.errors.bio)}
                helperText={formik.touched.bio && formik.errors.bio}
              />

            </Box>
          </Box>
        </Paper>
      </Container>

      {/* Floating Save Changes Button */}
      <Tooltip title="Save Profile" arrow>
        <Fab
          color="primary"
          aria-label="save profile"
          type="submit"
          disabled={formik.isSubmitting}
          onClick={formik.handleSubmit}
          sx={{
            position: 'fixed',
            bottom: 120,
            right: 24,
            backgroundColor: '#f43d65',
            '&:hover': {
              backgroundColor: '#f297ab',
            },
            '&:disabled': {
              backgroundColor: '#ccc',
            },
            zIndex: 1000
          }}
        >
          <Save />
        </Fab>
      </Tooltip>
    </PageContainer>
  );
};

export default EditProfile; 