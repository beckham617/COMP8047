import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import {
  Box,
  Container,
  Paper,
  TextField,
  Typography,
  IconButton,
  Alert,
  Snackbar,
  Fab,
  Tooltip
} from '@mui/material';
import {
  Add,
  Delete,
  Save
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';
import { pollsAPI } from '../services/api';

const CreatePoll = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [options, setOptions] = useState(['']);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');

  const validationSchema = Yup.object({
    question: Yup.string()
      .min(5, 'Question must be at least 5 characters')
      .max(200, 'Question must be at most 200 characters')
      .required('Question is required')
  });

  const formik = useFormik({
    initialValues: {
      question: ''
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        if (!user) {
          setSnackbarMessage('User not authenticated');
          setSnackbarSeverity('error');
          setSnackbarOpen(true);
          return;
        }

        // Validate that at least one option is provided
        const validOptions = options.filter(option => option.trim() !== '');
        if (validOptions.length < 2) {
          setSnackbarMessage('At least two options are required');
          setSnackbarSeverity('error');
          setSnackbarOpen(true);
          return;
        }

        // Create poll using backend API
        const pollRequest = {
          question: values.question,
          options: validOptions
        };

        await pollsAPI.createPoll(planId, pollRequest);

        setSnackbarMessage('Poll created successfully!');
        setSnackbarSeverity('success');
        setSnackbarOpen(true);

        // Navigate after a short delay to show success message
        setTimeout(() => {
          navigate(`/poll/${planId}`);
        }, 1000);
      } catch (err) {
        console.error('Failed to create poll:', err);
        setSnackbarMessage(err.message || 'Failed to create poll');
        setSnackbarSeverity('error');
        setSnackbarOpen(true);
      }
    }
  });

  const handleAddOption = (index) => {
    const newOptions = [...options];
    newOptions.splice(index + 1, 0, ''); // Insert new empty option after the current index
    setOptions(newOptions);
  };

  const handleRemoveOption = (index) => {
    if (options.length > 1) {
      setOptions(options.filter((_, i) => i !== index));
    }
  };

  const handleOptionChange = (index, value) => {
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);
  };

  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };

  return (
    <PageContainer>
      <PageHeader title="New Poll" onBack={() => navigate(`/poll/${planId}`)}>
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          Create a poll for your travel group
        </Typography>
      </PageHeader>

      <Container maxWidth="md" sx={{ py: 4, position: 'relative', zIndex: 1 }}>
        <Paper
          elevation={8}
          sx={{
            p: 2,
            borderRadius: 3,
            position: 'relative',
            backgroundColor: 'transparent',
            zIndex: 1,
          }}
        >
          <Box component="form" onSubmit={formik.handleSubmit} sx={{ p: 2 }}>
            <Box sx={{ mb: 4 }}>
              {/* Question */}
              <TextField
                name="question"
                label="Poll Question"
                value={formik.values.question}
                onChange={formik.handleChange}
                onBlur={formik.handleBlur}
                error={formik.touched.question && Boolean(formik.errors.question)}
                helperText={formik.touched.question && formik.errors.question}
                sx={{ width: '95%' }}
              />
            </Box>

            <Box>
              {/* Options */}
                <Typography variant="h6" gutterBottom>
                  Options
                </Typography>
                {options.map((option, index) => (
                  <Box key={index} sx={{ display: 'flex', gap: 1, mb: 2, width: '95%' }}>
                    <TextField
                      fullWidth
                      label={`Option ${index + 1}`}
                      value={option}
                      onChange={(e) => handleOptionChange(index, e.target.value)}
                      placeholder={`Enter option ${index + 1}`}
                    />
                    <IconButton
                      color="primary"
                      onClick={() => handleAddOption(index)}
                      sx={{ alignSelf: 'center' }}
                    >
                      <Add />
                    </IconButton>
                    <IconButton
                      color="error"
                      onClick={() => handleRemoveOption(index)}
                      sx={{ alignSelf: 'center' }}
                    >
                      <Delete />
                    </IconButton>
                  </Box>
                ))}
            </Box>
          </Box>
        </Paper>
      </Container>

      {/* Floating Action Button */}
      <Tooltip title="Create Poll" arrow>
        <Fab
          color="primary"
          aria-label="create poll"
          onClick={formik.handleSubmit}
          disabled={formik.isSubmitting || !formik.values.question.trim() || options.filter(opt => opt.trim() !== '').length < 2}
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
          <Save />
        </Fab>
      </Tooltip>

      {/* Snackbar for feedback */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert onClose={handleSnackbarClose} severity={snackbarSeverity} sx={{ width: '100%' }}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </PageContainer>
  );
};

export default CreatePoll; 