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
import { PhotoCamera, ArrowBack } from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import { usersAPI } from '../services/api';
import backgroundImage from '../assets/background.avif';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';

const Register = () => {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [profilePicture, setProfilePicture] = useState(null);
  const [error, setError] = useState('');

  const validationSchema = Yup.object({
    email: Yup.string()
      .email('Invalid email address')
      .required('Email is required'),
    password: Yup.string()
      .min(6, 'Password must be at least 6 characters')
      .required('Password is required'),
    confirmPassword: Yup.string()
      .oneOf([Yup.ref('password'), null], 'Passwords must match')
      .required('Confirm password is required'),
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
      .required('Primary language is required'),
    country: Yup.string()
      .required('Country is required'),
    city: Yup.string()
      .required('City is required'),
    bio: Yup.string()
      .max(500, 'Bio must be at most 500 characters'),
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
      email: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
      gender: '',
      birthYear: '',
      birthMonth: '',
      phoneNumber: '',
      language: '',
      additionalLanguages: '',
      city: '',
      country: '',
      bio: '',
      profilePicture: null
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        setError('');
        
        // Prepare user data for backend
        const userData = {
          email: values.email,
          password: values.password,
          firstName: values.firstName,
          lastName: values.lastName,
          gender: values.gender,
          birthYear: values.birthYear,
          birthMonth: values.birthMonth,
          phoneNumber: values.phoneNumber,
          language: values.language,
          additionalLanguages: values.additionalLanguages,
          city: values.city,
          country: values.country,
          bio: values.bio
        };

        // Upload profile picture first if selected
        if (values.profilePicture instanceof File) {
          try {
            const uploadResponse = await usersAPI.uploadProfilePicture(values.profilePicture);
            userData.profilePicture = uploadResponse.filePath;
          } catch (uploadError) {
            setError('Failed to upload profile picture: ' + uploadError.message);
            return;
          }
        }

        const result = await register(userData);
        
        if (result.success) {
          navigate('/discovery');
        } else {
          setError(result.error);
        }
      } catch (err) {
        setError(err.message || 'Registration failed. Please try again.');
      }
    }
  });

  const handleAvatarChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      formik.setFieldValue('profilePicture', file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setProfilePicture(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleBack = () => {
    navigate('/');
  };

  const countries = [
    'United States', 'Canada', 'United Kingdom', 'Australia', 'Germany',
    'France', 'Japan', 'China', 'India', 'Brazil', 'Mexico', 'Italy',
    'Spain', 'Netherlands', 'Sweden', 'Norway', 'Denmark', 'Finland',
    'Switzerland', 'Austria', 'Belgium', 'Portugal', 'Greece', 'Poland',
    'Czech Republic', 'Hungary', 'Romania', 'Bulgaria', 'Croatia',
    'Slovenia', 'Slovakia', 'Estonia', 'Latvia', 'Lithuania'
  ];

  const languages = [
    'English', 'Spanish', 'French', 'German', 'Italian', 'Portuguese',
    'Russian', 'Chinese', 'Japanese', 'Korean', 'Arabic', 'Hindi',
    'Dutch', 'Swedish', 'Norwegian', 'Danish', 'Finnish', 'Polish',
    'Czech', 'Hungarian', 'Romanian', 'Bulgarian', 'Greek', 'Turkish'
  ];

  const genders = [
    { value: 'MALE', label: 'Male' },
    { value: 'FEMALE', label: 'Female' },
    { value: 'OTHER', label: 'Other' }
  ];

  return (
    <PageContainer>
      <PageHeader title="Register" onBack={handleBack}>
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          Create your account to get started
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
                <TextField
                  fullWidth
                  name="password"
                  label="Password"
                  type="password"
                  value={formik.values.password}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.password && Boolean(formik.errors.password)}
                  helperText={formik.touched.password && formik.errors.password}
                />
                <TextField
                  fullWidth
                  name="confirmPassword"
                  label="Confirm Password"
                  type="password"
                  value={formik.values.confirmPassword}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.confirmPassword && Boolean(formik.errors.confirmPassword)}
                  helperText={formik.touched.confirmPassword && formik.errors.confirmPassword}
                />
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
              <FormControl fullWidth error={formik.touched.gender && Boolean(formik.errors.gender)}>
                <InputLabel>Gender</InputLabel>
                <Select
                  name="gender"
                  value={formik.values.gender}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  label="Gender"
                >
                  {genders.map((gender) => (
                    <MenuItem key={gender.value} value={gender.value}>
                      {gender.label}
                    </MenuItem>
                  ))}
                </Select>
                {formik.touched.gender && formik.errors.gender && (
                  <FormHelperText>{formik.errors.gender}</FormHelperText>
                )}
              </FormControl>
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
              <FormControl fullWidth error={formik.touched.language && Boolean(formik.errors.language)}>
                <InputLabel>Language</InputLabel>
                <Select
                  name="language"
                  value={formik.values.language}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  label="Primary Language"
                >
                  {languages.map((lang) => (
                    <MenuItem key={lang} value={lang}>
                      {lang}
                    </MenuItem>
                  ))}
                </Select>
                {formik.touched.language && formik.errors.language && (
                  <FormHelperText>{formik.errors.language}</FormHelperText>
                )}
              </FormControl>
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
                    backgroundColor: '#f43d65',
                    '&:hover': {
                    backgroundColor: '#f297ab',
                    }
                  }}
                >
                Register
                </Button>
            </Box>
          </Box>
        </Paper>
      </Container>
    </PageContainer>
  );
};

export default Register; 