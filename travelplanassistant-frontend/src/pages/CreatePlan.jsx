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
  Grid,
  IconButton,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
  Card,
  CardMedia,
  CardContent
} from '@mui/material';
import {
  ArrowBack,
  PhotoCamera,
  Delete,
  Add
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const CreatePlan = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [images, setImages] = useState([]);
  const [error, setError] = useState('');

  const validationSchema = Yup.object({
    title: Yup.string()
      .min(3, 'Title must be at least 3 characters')
      .max(100, 'Title must be at most 100 characters')
      .required('Title is required'),
    description: Yup.string()
      .min(10, 'Description must be at least 10 characters')
      .max(500, 'Description must be at most 500 characters')
      .required('Description is required'),
    startDate: Yup.date()
      .min(new Date(), 'Start date must be in the future')
      .required('Start date is required'),
    endDate: Yup.date()
      .min(Yup.ref('startDate'), 'End date must be after start date')
      .required('End date is required'),
    location: Yup.string()
      .required('Location is required'),
    maxMembers: Yup.number()
      .min(2, 'Minimum 2 members')
      .max(20, 'Maximum 20 members')
      .required('Maximum members is required'),
    visibility: Yup.string()
      .required('Visibility is required')
  });

  const formik = useFormik({
    initialValues: {
      title: '',
      description: '',
      startDate: '',
      endDate: '',
      location: '',
      maxMembers: 4,
      visibility: 'PUBLIC'
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        setError('');
        
        if (!user) {
          setError('User not authenticated');
          return;
        }

        const newPlan = {
          id: Date.now().toString(),
          title: values.title,
          description: values.description,
          startDate: values.startDate,
          endDate: values.endDate,
          location: values.location,
          maxMembers: values.maxMembers,
          visibility: values.visibility,
          planStatus: 'new',
          coverImage: images[0] || 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
          images: images,
          owner: {
            id: user.id,
            firstName: user.firstName,
            lastName: user.lastName,
            avatar: user.avatar
          },
          members: [
            {
              userId: user.id,
              userPlanStatus: 'owned',
              joinedAt: new Date().toISOString()
            }
          ],
          createdAt: new Date().toISOString()
        };

        // Store plan in localStorage
        const plans = JSON.parse(localStorage.getItem('plans') || '[]');
        localStorage.setItem('plans', JSON.stringify([...plans, newPlan]));

        navigate('/my-plans');
      } catch (err) {
        setError('Failed to create plan. Please try again.');
      }
    }
  });

  const handleImageUpload = (event) => {
    const files = Array.from(event.target.files);
    const validFiles = files.filter(file => 
      file.type.startsWith('image/') && file.size <= 5000000 // 5MB limit
    );

    if (validFiles.length + images.length > 5) {
      setError('Maximum 5 images allowed');
      return;
    }

    validFiles.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e) => {
        setImages(prev => [...prev, e.target.result]);
      };
      reader.readAsDataURL(file);
    });
  };

  const handleRemoveImage = (index) => {
    setImages(prev => prev.filter((_, i) => i !== index));
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
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <IconButton
              onClick={() => navigate('/my-plans')}
              sx={{ color: 'white', mr: 2 }}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h5" component="h1" sx={{ fontWeight: 'bold' }}>
              Create New Plan
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
              {/* Title */}
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  name="title"
                  label="Plan Title"
                  value={formik.values.title}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.title && Boolean(formik.errors.title)}
                  helperText={formik.touched.title && formik.errors.title}
                />
              </Grid>

              {/* Description */}
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  name="description"
                  label="Description"
                  multiline
                  rows={4}
                  value={formik.values.description}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.description && Boolean(formik.errors.description)}
                  helperText={formik.touched.description && formik.errors.description}
                />
              </Grid>

              {/* Start Date */}
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  name="startDate"
                  label="Start Date"
                  type="date"
                  value={formik.values.startDate}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.startDate && Boolean(formik.errors.startDate)}
                  helperText={formik.touched.startDate && formik.errors.startDate}
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
              </Grid>

              {/* End Date */}
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  name="endDate"
                  label="End Date"
                  type="date"
                  value={formik.values.endDate}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.endDate && Boolean(formik.errors.endDate)}
                  helperText={formik.touched.endDate && formik.errors.endDate}
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
              </Grid>

              {/* Location */}
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  name="location"
                  label="Location"
                  value={formik.values.location}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.location && Boolean(formik.errors.location)}
                  helperText={formik.touched.location && formik.errors.location}
                />
              </Grid>

              {/* Max Members */}
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  name="maxMembers"
                  label="Maximum Members"
                  type="number"
                  value={formik.values.maxMembers}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.maxMembers && Boolean(formik.errors.maxMembers)}
                  helperText={formik.touched.maxMembers && formik.errors.maxMembers}
                  inputProps={{ min: 2, max: 20 }}
                />
              </Grid>

              {/* Visibility */}
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth error={formik.touched.visibility && Boolean(formik.errors.visibility)}>
                  <InputLabel>Visibility</InputLabel>
                  <Select
                    name="visibility"
                    value={formik.values.visibility}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    label="Visibility"
                  >
                    <MenuItem value="PUBLIC">Public</MenuItem>
                    <MenuItem value="PRIVATE">Private</MenuItem>
                  </Select>
                  {formik.touched.visibility && formik.errors.visibility && (
                    <FormHelperText>{formik.errors.visibility}</FormHelperText>
                  )}
                </FormControl>
              </Grid>

              {/* Image Upload */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom>
                  Upload Images (Optional)
                </Typography>
                <Box sx={{ mb: 2 }}>
                  <Button
                    variant="outlined"
                    component="label"
                    startIcon={<PhotoCamera />}
                    disabled={images.length >= 5}
                  >
                    Add Images
                    <input
                      hidden
                      accept="image/*"
                      multiple
                      type="file"
                      onChange={handleImageUpload}
                    />
                  </Button>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                    Maximum 5 images, 5MB each
                  </Typography>
                </Box>

                {/* Image Preview */}
                {images.length > 0 && (
                  <Grid container spacing={2}>
                    {images.map((image, index) => (
                      <Grid item xs={6} sm={4} md={3} key={index}>
                        <Card>
                          <CardMedia
                            component="img"
                            height="140"
                            image={image}
                            alt={`Upload ${index + 1}`}
                          />
                          <CardContent sx={{ p: 1 }}>
                            <IconButton
                              size="small"
                              color="error"
                              onClick={() => handleRemoveImage(index)}
                              sx={{ float: 'right' }}
                            >
                              <Delete />
                            </IconButton>
                          </CardContent>
                        </Card>
                      </Grid>
                    ))}
                  </Grid>
                )}
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
                  {formik.isSubmitting ? 'Creating Plan...' : 'Create Plan'}
                </Button>
              </Grid>
            </Grid>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default CreatePlan; 