import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { travelPlansAPI, usersAPI } from '../services/api';
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
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
  Card,
  CardMedia,
  IconButton,
  Fab,
  Tooltip
} from '@mui/material';
import {
  PhotoCamera,
  Close,
  Save
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import backgroundImage from '../assets/background.avif';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';
import DEFAULT_PLAN_COVER_IMAGE from '../config/config'

const CreatePlan = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [error, setError] = useState('');

  // 1. Add new state for all new fields, update formik initialValues and validationSchema
  const [planType, setPlanType] = useState('TRAVEL');
  const [category, setCategory] = useState('ADVENTURE');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [origin, setOrigin] = useState('');
  const [destination, setDestination] = useState('');
  const [transportation, setTransportation] = useState('FLIGHT');
  const [accommodation, setAccommodation] = useState('HOTEL');
  const [genderRestriction, setGenderRestriction] = useState('NONE');
  const [ageRange, setAgeRange] = useState('NONE');
  const [languageRestriction, setLanguageRestriction] = useState('NONE');

  // Country, city, language options (reuse from Register.jsx for now)
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
  const categories = [
    'Trip', 'Sports', 'Game', 'Match', 'Event', 'Concert', 'Show', 'Family time'
  ];
  const planTypes = [
    { value: 'PUBLIC', label: 'Public' },
    { value: 'PRIVATE', label: 'Private' }
  ];
  const transportationTypes = [
    'Flight', 'Railway', 'Car', 'Cruise', 'Other'
  ];
  const accommodationTypes = [
    'Hotel', 'Hostel', 'Airbnb', 'Camping', 'Other'
  ];
  const genderOptions = [
    { value: 'ANY', label: 'Any' },
    { value: 'MALE', label: 'Male' },
    { value: 'FEMALE', label: 'Female' },
    { value: 'OTHER', label: 'Other' }
  ];

  const validationSchema = Yup.object({
    title: Yup.string()
      .min(3, 'Title must be at least 3 characters')
      .max(100, 'Title must be at most 100 characters')
      .required('Title is required'),
    description: Yup.string()
      .min(10, 'Description must be at least 10 characters')
      .max(1000, 'Description must be at most 1000 characters')
      .required('Description is required'),
    startDate: Yup.date()
      .min(new Date(), 'Start date cannot be in the past')
      .required('Start date is required'),
    endDate: Yup.date()
      .min(Yup.ref('startDate'), 'End date must be after start date')
      .required('End date is required'),
    location: Yup.string()
      .min(2, 'Location must be at least 2 characters')
      .max(100, 'Location must be at most 100 characters')
      .required('Location is required'),
    maxMembers: Yup.number()
      .min(2, 'Minimum 2 members')
      .max(20, 'Maximum 20 members')
      .required('Maximum members is required'),
    visibility: Yup.string()
      .oneOf(['PUBLIC', 'PRIVATE'], 'Please select a valid visibility')
      .required('Visibility is required')
  });

  // Update formik initialValues and validationSchema for all new fields
  const formik = useFormik({
    initialValues: {
      title: '',
      planType: 'PUBLIC',
      category: '',
      startDate: '',
      endDate: '',
      originCountry: '',
      originCity: '',
      destinationCountry: '',
      destinationCity: '',
      transportation: '',
      accommodation: '',
      maxMembers: 5,
      description: '',
      gender: 'ANY',
      ageMin: '',
      ageMax: '',
      language: '',
      images: []
    },
    validationSchema: Yup.object({
      title: Yup.string().min(3, 'Title must be at least 3 characters').max(100, 'Title must be at most 100 characters').required('Title is required'),
      planType: Yup.string().oneOf(['PUBLIC', 'PRIVATE']).required('Plan type is required'),
      category: Yup.string().oneOf(categories).required('Category is required'),
      startDate: Yup.date().min(new Date(), 'Start date cannot be in the past').required('Start date is required'),
      endDate: Yup.date().min(Yup.ref('startDate'), 'End date must be after start date').required('End date is required'),
      originCountry: Yup.string().oneOf(countries).required('Origin country is required'),
      originCity: Yup.string().required('Origin city is required'),
      destinationCountry: Yup.string().oneOf(countries).required('Destination country is required'),
      destinationCity: Yup.string().required('Destination city is required'),
      transportation: Yup.string().oneOf(transportationTypes).required('Type of transportation is required'),
      accommodation: Yup.string().oneOf(accommodationTypes).required('Type of accommodation is required'),
      maxMembers: Yup.number().min(2, 'Minimum 2 members').max(20, 'Maximum 20 members').required('Maximum members is required'),
      description: Yup.string().min(10, 'Description must be at least 10 characters').max(1000, 'Description must be at most 1000 characters').required('Description is required'),
      gender: Yup.string().oneOf(genderOptions.map(g => g.value)).required('Gender restriction is required'),
      ageMin: Yup.number().min(0, 'Minimum age must be at least 0').max(120, 'Minimum age too high').required('Minimum age is required'),
      ageMax: Yup.number().min(Yup.ref('ageMin'), 'Max age must be greater than min age').max(120, 'Maximum age too high').required('Maximum age is required'),
      language: Yup.string().oneOf(languages).required('Language is required'),
      images: Yup.array().max(3, 'You can upload up to 3 images')
    }),
    onSubmit: async (values) => {
      try {
        setError('');
        console.log(JSON.stringify(values))
        // return;
        // Create plan data
        const planData = {
          ...values,
          // id: Date.now().toString(),
          owner: {
            id: user.id,
            firstName: user.firstName,
            lastName: user.lastName,
            profilePicture: user.profilePicture
          },
          // members: [
          //   {
          //     userId: user.id,
          //     userPlanStatus: 'owned',
          //     joinedAt: new Date().toISOString()
          //   }
          // ],
          status: 'NEW',
          // images now contains array of uploaded file paths (no binary)
          // coverImage: values.images?.[0] || DEFAULT_PLAN_COVER_IMAGE,
          images: Array.isArray(values.images) ? values.images : [],
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        };

        planData.category = planData.category.toLocaleUpperCase();
        planData.transportation = planData.transportation.toLocaleUpperCase();
        planData.accommodation = planData.accommodation.toLocaleUpperCase();

        // Save to localStorage
        // const existingPlans = JSON.parse(localStorage.getItem('plans') || '[]');
        // existingPlans.push(planData);
        // localStorage.setItem('plans', JSON.stringify(existingPlans));
        
        await travelPlansAPI.createPlan(planData);

        navigate('/my-plans');
      } catch (err) {
        setError(err.message || 'Failed to create plan. Please try again.');
      }
    }
  });

  const handleImageUpload = async (event) => {
    const file = event.target.files && event.target.files[0];
    if (!file) return;
    try {
      const uploadResponse = await usersAPI.uploadProfilePicture(file);
      const filePath = uploadResponse.filePath;
      const current = Array.isArray(formik.values.images) ? formik.values.images : [];
      if (current.length >= 3) return;
      formik.setFieldValue('images', [...current, filePath]);
    } catch (uploadError) {
      setError('Failed to upload image: ' + (uploadError.message || 'Unknown error'));
    } finally {
      // allow selecting the same file again
      event.target.value = '';
    }
  };

  const handleRemoveImage = (index) => {
    const current = Array.isArray(formik.values.images) ? formik.values.images : [];
    formik.setFieldValue('images', current.filter((_, i) => i !== index));
  };

  const handleBack = () => {
    navigate('/my-plans');
  };

  return (
    <PageContainer>
        <PageHeader title="Create Plan" onBack={handleBack} >
          <Typography variant="subtitle1" sx={{ mt: 3 }}>
            Create a new plan here
          </Typography>
        </PageHeader>

      {/* Form */}
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
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={formik.handleSubmit}>
            {/* Plan Detail Section */}
            <Typography variant="h6" sx={{ mb: 2 }}>Plan Details</Typography>
            <TextField fullWidth name="title" label="Plan Title" value={formik.values.title} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.title && Boolean(formik.errors.title)} helperText={formik.touched.title && formik.errors.title} sx={{ mb: 3 }} />
            <FormControl fullWidth error={formik.touched.planType && Boolean(formik.errors.planType)} sx={{ mb: 3 }}>
              <InputLabel>Plan Type</InputLabel>
              <Select name="planType" value={formik.values.planType} onChange={formik.handleChange} onBlur={formik.handleBlur} label="Plan Type">
                {planTypes.map(opt => <MenuItem key={opt.value} value={opt.value}>{opt.label}</MenuItem>)}
              </Select>
              {formik.touched.planType && formik.errors.planType && <FormHelperText>{formik.errors.planType}</FormHelperText>}
            </FormControl>
            <FormControl fullWidth error={formik.touched.category && Boolean(formik.errors.category)} sx={{ mb: 3 }}>
              <InputLabel>Category</InputLabel>
              <Select name="category" value={formik.values.category} onChange={formik.handleChange} onBlur={formik.handleBlur} label="Category">
                {categories.map(cat => <MenuItem key={cat} value={cat}>{cat}</MenuItem>)}
              </Select>
              {formik.touched.category && formik.errors.category && <FormHelperText>{formik.errors.category}</FormHelperText>}
            </FormControl>
            <TextField fullWidth name="startDate" label="Start Date" type="date" InputLabelProps={{ shrink: true }} value={formik.values.startDate} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.startDate && Boolean(formik.errors.startDate)} helperText={formik.touched.startDate && formik.errors.startDate} sx={{ mb: 3 }} />
            <TextField fullWidth name="endDate" label="End Date" type="date" InputLabelProps={{ shrink: true }} value={formik.values.endDate} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.endDate && Boolean(formik.errors.endDate)} helperText={formik.touched.endDate && formik.errors.endDate} sx={{ mb: 3 }} />
            <FormControl fullWidth error={formik.touched.originCountry && Boolean(formik.errors.originCountry)} sx={{ mb: 3 }}>
              <InputLabel>Origin Country</InputLabel>
              <Select name="originCountry" value={formik.values.originCountry} onChange={formik.handleChange} onBlur={formik.handleBlur} label="Origin Country">
                {countries.map(country => <MenuItem key={country} value={country}>{country}</MenuItem>)}
              </Select>
              {formik.touched.originCountry && formik.errors.originCountry && <FormHelperText>{formik.errors.originCountry}</FormHelperText>}
            </FormControl>
            <TextField fullWidth name="originCity" label="Origin City" value={formik.values.originCity} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.originCity && Boolean(formik.errors.originCity)} helperText={formik.touched.originCity && formik.errors.originCity} sx={{ mb: 3 }} />
            <FormControl fullWidth error={formik.touched.destinationCountry && Boolean(formik.errors.destinationCountry)} sx={{ mb: 3 }}>
              <InputLabel>Destination Country</InputLabel>
              <Select name="destinationCountry" value={formik.values.destinationCountry} onChange={formik.handleChange} onBlur={formik.handleBlur} label="Destination Country">
                {countries.map(country => <MenuItem key={country} value={country}>{country}</MenuItem>)}
              </Select>
              {formik.touched.destinationCountry && formik.errors.destinationCountry && <FormHelperText>{formik.errors.destinationCountry}</FormHelperText>}
            </FormControl>
            <TextField fullWidth name="destinationCity" label="Destination City" value={formik.values.destinationCity} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.destinationCity && Boolean(formik.errors.destinationCity)} helperText={formik.touched.destinationCity && formik.errors.destinationCity} sx={{ mb: 3 }} />
            <FormControl fullWidth error={formik.touched.transportation && Boolean(formik.errors.transportation)} sx={{ mb: 3 }}>
              <InputLabel>Transportation</InputLabel>
              <Select name="transportation" value={formik.values.transportation} onChange={formik.handleChange} onBlur={formik.handleBlur} label="Transportation">
                {transportationTypes.map(type => <MenuItem key={type} value={type}>{type}</MenuItem>)}
              </Select>
              {formik.touched.transportation && formik.errors.transportation && <FormHelperText>{formik.errors.transportation}</FormHelperText>}
            </FormControl>
            <FormControl fullWidth error={formik.touched.accommodation && Boolean(formik.errors.accommodation)} sx={{ mb: 3 }}>
              <InputLabel>Accommodation</InputLabel>
              <Select name="accommodation" value={formik.values.accommodation} onChange={formik.handleChange} onBlur={formik.handleBlur} label="Accommodation">
                {accommodationTypes.map(type => <MenuItem key={type} value={type}>{type}</MenuItem>)}
              </Select>
              {formik.touched.accommodation && formik.errors.accommodation && <FormHelperText>{formik.errors.accommodation}</FormHelperText>}
            </FormControl>
            <TextField fullWidth name="maxMembers" label="Maximum Member Number" type="number" value={formik.values.maxMembers} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.maxMembers && Boolean(formik.errors.maxMembers)} helperText={formik.touched.maxMembers && formik.errors.maxMembers} sx={{ mb: 3 }} />
            <TextField fullWidth name="description" label="Description (max 1000 characters)" multiline rows={4} value={formik.values.description} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.description && Boolean(formik.errors.description)} helperText={formik.touched.description && formik.errors.description} inputProps={{ maxLength: 1000 }} sx={{ mb: 3 }} />
            {/* Pictures upload */}
            <Grid item xs={12}>
              <Typography variant="subtitle1" sx={{ mb: 1 }}>Pictures (up to 3)</Typography>
              <Box sx={{ display: 'flex', gap: 2 }}>
                {formik.values.images && formik.values.images.map((img, idx) => (
                  <Card key={idx} sx={{ width: 120, height: 120, position: 'relative' }}>
                    <CardMedia component="img" image={usersAPI.getProfilePicture(img)} alt={`Plan image ${idx + 1}`} sx={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    <IconButton size="small" sx={{ position: 'absolute', top: 2, right: 2, background: 'rgba(255,255,255,0.7)' }} onClick={() => {
                      handleRemoveImage(idx);
                    }}>
                      <Close fontSize="small" />
                    </IconButton>
                  </Card>
                ))}
                {formik.values.images.length < 3 && (
                  <Box sx={{ width: 120, height: 120, display: 'flex', alignItems: 'center', justifyContent: 'center', border: '2px dashed #ccc', borderRadius: 2, cursor: 'pointer', position: 'relative' }}>
                    <input type="file" accept="image/*" style={{ display: 'none' }} id="plan-image-upload" onChange={handleImageUpload} />
                    <label htmlFor="plan-image-upload">
                      <IconButton component="span" color="primary">
                        <PhotoCamera />
                      </IconButton>
                    </label>
                  </Box>
                )}
              </Box>
              {formik.touched.images && formik.errors.images && <FormHelperText error>{formik.errors.images}</FormHelperText>}
            </Grid>

            {/* Restriction Section */}
            <Typography variant="h6" sx={{ mt: 4, mb: 2 }}>Restrictions</Typography>
            <FormControl fullWidth error={formik.touched.gender && Boolean(formik.errors.gender)} sx={{ mb: 3 }}>
              <InputLabel>Gender</InputLabel>
              <Select name="gender" value={formik.values.gender} onChange={formik.handleChange} onBlur={formik.handleBlur} label="Gender">
                {genderOptions.map(opt => <MenuItem key={opt.value} value={opt.value}>{opt.label}</MenuItem>)}
              </Select>
              {formik.touched.gender && formik.errors.gender && <FormHelperText>{formik.errors.gender}</FormHelperText>}
            </FormControl>
            <TextField fullWidth name="ageMin" label="Age Min" type="number" value={formik.values.ageMin} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.ageMin && Boolean(formik.errors.ageMin)} helperText={formik.touched.ageMin && formik.errors.ageMin} sx={{ mb: 3 }} />
            <TextField fullWidth name="ageMax" label="Age Max" type="number" value={formik.values.ageMax} onChange={formik.handleChange} onBlur={formik.handleBlur} error={formik.touched.ageMax && Boolean(formik.errors.ageMax)} helperText={formik.touched.ageMax && formik.errors.ageMax} sx={{ mb: 3 }} />
            <FormControl fullWidth error={formik.touched.language && Boolean(formik.errors.language)} sx={{ mb: 3 }}>
              <InputLabel>Language</InputLabel>
              <Select name="language" value={formik.values.language} onChange={formik.handleChange} onBlur={formik.handleBlur} label="Language">
                {languages.map(lang => <MenuItem key={lang} value={lang}>{lang}</MenuItem>)}
              </Select>
              {formik.touched.language && formik.errors.language && <FormHelperText>{formik.errors.language}</FormHelperText>}
            </FormControl>

            {/* Members Section */}
            <Typography variant="h6" sx={{ mt: 4, mb: 2 }}>Members</Typography>
            <Paper variant="outlined" sx={{ p: 2, mb: 2, background: 'transparent' }}>
              <Typography variant="body1">You can invite members after you've saved this plan.</Typography>
            </Paper>
          </Box>
        </Paper>
      </Container>

      {/* Floating Save Changes Button */}
      <Tooltip title="Save Plan" arrow>
        <Fab
          color="primary"
          aria-label="save plan"
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

export default CreatePlan; 