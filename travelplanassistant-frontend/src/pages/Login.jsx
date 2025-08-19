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
  Alert,
  Grid
} from '@mui/material';
import { ArrowBack } from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import backgroundImage from '../assets/background.avif';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';

const Login = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [error, setError] = useState('');

  const validationSchema = Yup.object({
    email: Yup.string()
      .email('Invalid email address')
      .required('Email is required'),
    password: Yup.string()
      .required('Password is required')
  });

  const formik = useFormik({
    initialValues: {
      email: '',
      password: ''
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        setError('');
        
        const result = await login(values.email, values.password);
        
        if (result.success) {
          navigate('/discovery');
        } else {
          setError(result.error);
        }
      } catch (err) {
        setError('Login failed. Please try again.');
      }
    }
  });

  const handleBack = () => {
    navigate('/');
  };

  return (
    <PageContainer>
      <PageHeader title="Log In" onBack={handleBack}>
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          Welcome back! Please log in to continue
        </Typography>
      </PageHeader>
      <Container maxWidth="sm">
        <Paper
          elevation={8}
          sx={{
            p: 4,
            borderRadius: 3,
            position: 'relative',
            backgroundColor: 'transparent',
          }}
        >

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={formik.handleSubmit}>
            <Box sx={{ 
              display: 'flex', 
              flexDirection: 'column', 
              alignItems: 'center',
              maxWidth: 400,
              mx: 'auto'
            }}>
              <Box sx={{ width: '100%', mb: 3 }}>
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
                  sx={{ mb: 3 }}
                />

                {/* Password */}
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
                  sx={{ mb: 3 }}
                />
              </Box>

              {/* Submit Button */}
              <Box sx={{ width: '100%', display: 'flex', justifyContent: 'flex-end' }}>
                <Button
                  type="submit"
                  sx={{
                    py: 1.5,
                    px: 4,
                    fontSize: '1.1rem',
                    fontWeight: 'bold',
                    backgroundColor: '#f43d65',
                    '&:hover': {
                      backgroundColor: '#f297ab',
                    }
                  }}
                  variant="contained"
                  size="large"
                  disabled={formik.isSubmitting}
                >
                  {formik.isSubmitting ? 'Logging In...' : 'Log In'}
                </Button>
              </Box>

              {/* Register Link */}
              <Box sx={{ textAlign: 'center', mt: 3 }}>
                <Box sx={{ textAlign: 'center' }}>
                  <Typography variant="body2" color="text.secondary">
                    Don't have an account?{' '}
                    <Button
                      onClick={() => navigate('/register')}
                      sx={{
                        textTransform: 'none',
                        color: '#1976d2',
                        '&:hover': {
                          backgroundColor: 'transparent',
                          textDecoration: 'underline'
                        }
                      }}
                    >
                      Register here
                    </Button>
                  </Typography>
                </Box>
              </Box>
            </Box>
          </Box>
        </Paper>
      </Container>
    </PageContainer>
  );
};

export default Login; 