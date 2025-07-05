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
  Grid,
  InputAdornment
} from '@mui/material';
import {
  ArrowBack,
  Add,
  Remove
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const CreateSharedExpense = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [personCount, setPersonCount] = useState(1);

  const validationSchema = Yup.object({
    title: Yup.string()
      .min(3, 'Title must be at least 3 characters')
      .max(100, 'Title must be at most 100 characters')
      .required('Title is required'),
    totalAmount: Yup.number()
      .positive('Amount must be positive')
      .required('Total amount is required')
  });

  const formik = useFormik({
    initialValues: {
      title: '',
      totalAmount: ''
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        if (!user) {
          throw new Error('User not authenticated');
        }

        const newExpense = {
          id: Date.now().toString(),
          title: values.title,
          totalAmount: parseFloat(values.totalAmount),
          personCount: personCount,
          perPerson: parseFloat(values.totalAmount) / personCount,
          createdBy: `${user.firstName} ${user.lastName}`,
          createdAt: new Date().toISOString()
        };

        // Store expense in localStorage
        const expensesKey = `expenses_${planId}`;
        const existingExpenses = JSON.parse(localStorage.getItem(expensesKey) || '[]');
        localStorage.setItem(expensesKey, JSON.stringify([...existingExpenses, newExpense]));

        navigate(`/expense/${planId}`);
      } catch (err) {
        console.error('Failed to create expense:', err);
      }
    }
  });

  const handlePersonCountChange = (increment) => {
    const newCount = personCount + increment;
    if (newCount >= 1 && newCount <= 20) {
      setPersonCount(newCount);
    }
  };

  const calculatePerPerson = () => {
    const amount = parseFloat(formik.values.totalAmount) || 0;
    return personCount > 0 ? amount / personCount : 0;
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
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
              New Shared Expense
            </Typography>
          </Box>
        </Container>
      </Box>

      {/* Form */}
      <Container maxWidth="md" sx={{ py: 3 }}>
        <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
          <Box component="form" onSubmit={formik.handleSubmit}>
            <Grid container spacing={3}>
              {/* Title */}
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  name="title"
                  label="Expense Title"
                  value={formik.values.title}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.title && Boolean(formik.errors.title)}
                  helperText={formik.touched.title && formik.errors.title}
                />
              </Grid>

              {/* Total Amount */}
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  name="totalAmount"
                  label="Total Amount"
                  type="number"
                  value={formik.values.totalAmount}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.totalAmount && Boolean(formik.errors.totalAmount)}
                  helperText={formik.touched.totalAmount && formik.errors.totalAmount}
                  InputProps={{
                    startAdornment: <InputAdornment position="start">$</InputAdornment>,
                  }}
                  inputProps={{ min: 0, step: 0.01 }}
                />
              </Grid>

              {/* Person Count */}
              <Grid item xs={12} sm={6}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Typography variant="body1" sx={{ minWidth: 'fit-content' }}>
                    Split between:
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <IconButton
                      onClick={() => handlePersonCountChange(-1)}
                      disabled={personCount <= 1}
                      size="small"
                    >
                      <Remove />
                    </IconButton>
                    <Typography variant="h6" sx={{ minWidth: 40, textAlign: 'center' }}>
                      {personCount}
                    </Typography>
                    <IconButton
                      onClick={() => handlePersonCountChange(1)}
                      disabled={personCount >= 20}
                      size="small"
                    >
                      <Add />
                    </IconButton>
                  </Box>
                </Box>
              </Grid>

              {/* Per Person Amount */}
              <Grid item xs={12}>
                <Paper elevation={1} sx={{ p: 3, backgroundColor: '#f5f5f5' }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Typography variant="h6">
                      Amount per person:
                    </Typography>
                    <Typography variant="h5" color="primary" sx={{ fontWeight: 'bold' }}>
                      {formatCurrency(calculatePerPerson())}
                    </Typography>
                  </Box>
                </Paper>
              </Grid>

              {/* Submit Button */}
              <Grid item xs={12}>
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={formik.isSubmitting || !formik.values.title || !formik.values.totalAmount}
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
                  {formik.isSubmitting ? 'Creating Expense...' : 'Create Expense'}
                </Button>
              </Grid>
            </Grid>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
};

export default CreateSharedExpense; 