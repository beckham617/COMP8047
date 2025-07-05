import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import {
  Box,
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  IconButton,
  Alert,
  Grid
} from '@mui/material';
import {
  ArrowBack,
  Add,
  Delete
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const CreatePoll = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [options, setOptions] = useState(['']);

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
          throw new Error('User not authenticated');
        }

        // Validate that at least one option is provided
        const validOptions = options.filter(option => option.trim() !== '');
        if (validOptions.length === 0) {
          throw new Error('At least one option is required');
        }

        const newPoll = {
          id: Date.now().toString(),
          question: values.question,
          options: validOptions,
          votes: [],
          createdBy: `${user.firstName} ${user.lastName}`,
          createdAt: new Date().toISOString()
        };

        // Store poll in localStorage
        const pollsKey = `polls_${planId}`;
        const existingPolls = JSON.parse(localStorage.getItem(pollsKey) || '[]');
        localStorage.setItem(pollsKey, JSON.stringify([...existingPolls, newPoll]));

        navigate(`/poll/${planId}`);
      } catch (err) {
        console.error('Failed to create poll:', err);
      }
    }
  });

  const handleAddOption = () => {
    setOptions([...options, '']);
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
              New Poll
            </Typography>
          </Box>
        </Container>
      </Box>

      {/* Form */}
      <Container maxWidth="md" sx={{ py: 3 }}>
        <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
          <Box component="form" onSubmit={formik.handleSubmit}>
            <Grid container spacing={3}>
              {/* Question */}
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  name="question"
                  label="Poll Question"
                  multiline
                  rows={3}
                  value={formik.values.question}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.question && Boolean(formik.errors.question)}
                  helperText={formik.touched.question && formik.errors.question}
                />
              </Grid>

              {/* Options */}
              <Grid item xs={12}>
                <Typography variant="h6" gutterBottom>
                  Options
                </Typography>
                {options.map((option, index) => (
                  <Box key={index} sx={{ display: 'flex', gap: 1, mb: 2 }}>
                    <TextField
                      fullWidth
                      label={`Option ${index + 1}`}
                      value={option}
                      onChange={(e) => handleOptionChange(index, e.target.value)}
                      placeholder={`Enter option ${index + 1}`}
                    />
                    {options.length > 1 && (
                      <IconButton
                        color="error"
                        onClick={() => handleRemoveOption(index)}
                        sx={{ alignSelf: 'center' }}
                      >
                        <Delete />
                      </IconButton>
                    )}
                  </Box>
                ))}
                <Button
                  variant="outlined"
                  startIcon={<Add />}
                  onClick={handleAddOption}
                  sx={{ mt: 1 }}
                >
                  Add Option
                </Button>
              </Grid>

              {/* Submit Button */}
              <Grid item xs={12}>
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={formik.isSubmitting || options.filter(opt => opt.trim() !== '').length === 0}
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
                  {formik.isSubmitting ? 'Creating Poll...' : 'Create Poll'}
                </Button>
              </Grid>
            </Grid>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default CreatePoll; 