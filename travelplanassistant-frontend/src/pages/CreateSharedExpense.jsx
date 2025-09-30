import React, { useState, useEffect } from 'react';
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
  InputAdornment,
  Fab,
  Tooltip,
  FormControlLabel,
  Checkbox,
  List,
  ListItem,
  ListItemText,
  Avatar,
  CircularProgress,
  Divider
} from '@mui/material';
import {
  Save
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';
import { travelPlansAPI, usersAPI, expensesAPI } from '../services/api';

const CreateSharedExpense = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [planMembers, setPlanMembers] = useState([]);
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [loadingMembers, setLoadingMembers] = useState(true);

  // Load plan members
  useEffect(() => {
    const loadPlanMembers = async () => {
      try {
        setLoadingMembers(true);
        const planData = await travelPlansAPI.getPlanDetails(planId);
        const members = Array.isArray(planData.members) ? planData.members : [];
        
        // Filter to only show accepted members
        const acceptedMembers = members.filter(member => 
          ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'].includes(
            (member.userPlanStatus || '').toString().toUpperCase()
          )
        );
        
        setPlanMembers(acceptedMembers);
        
        // Auto-select current user if they're in the plan
        const currentUserMember = acceptedMembers.find(member => member.userId === user?.id);
        if (currentUserMember) {
          setSelectedMembers([currentUserMember.userId]);
        }
      } catch (err) {
        console.error('Failed to load plan members:', err);
        setPlanMembers([]);
      } finally {
        setLoadingMembers(false);
      }
    };

    if (planId) {
      loadPlanMembers();
    }
  }, [planId, user]);

  const validationSchema = Yup.object({
    title: Yup.string()
      .min(3, 'Title must be at least 3 characters')
      .max(100, 'Title must be at most 100 characters')
      .required('Title is required'),
    description: Yup.string()
      .min(3, 'Description must be at least 3 characters')
      .max(500, 'Description must be at most 500 characters')
      .required('Description is required'),
    totalAmount: Yup.number()
      .positive('Amount must be positive')
      .required('Total amount is required')
  });

  const formik = useFormik({
    initialValues: {
      title: '',
      description: '',
      totalAmount: ''
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        if (!user) {
          throw new Error('User not authenticated');
        }

        if (selectedMembers.length === 0) {
          throw new Error('Please select at least one member to split the expense');
        }

        // Calculate amount per person
        const totalAmount = parseFloat(values.totalAmount);
        const amountPerPerson = totalAmount / selectedMembers.length;

        // Create participants array with selected members
        const participants = selectedMembers.map(memberId => ({
          userId: parseInt(memberId),
          amountOwed: amountPerPerson,
          isPaid: false
        }));

        // Prepare shared expense request according to DTO
        const sharedExpenseRequest = {
          description: values.description,
          purpose: values.title,
          totalAmount: totalAmount,
          currency: 'CAD',
          expenseDate: new Date().toISOString(),
          participants: participants
        };

        // Create shared expense using API
        await expensesAPI.createSharedExpense(planId, sharedExpenseRequest);
        navigate(`/expense/${planId}`);
      } catch (err) {
        console.error('Failed to create shared expense:', err);
        alert(err.message || 'Failed to create shared expense');
      }
    }
  });

  const handleMemberToggle = (memberId) => {
    setSelectedMembers(prev => {
      if (prev.includes(memberId)) {
        return prev.filter(id => id !== memberId);
      } else {
        return [...prev, memberId];
      }
    });
  };


  const calculatePerPerson = () => {
    const amount = parseFloat(formik.values.totalAmount) || 0;
    return selectedMembers.length > 0 ? amount / selectedMembers.length : 0;
  };

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

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  return (
    <PageContainer>
      <PageHeader title="New Shared Expense" onBack={() => navigate(`/expense/${planId}`)}>
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          Create and split a new expense
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
            {/* Basic Information Section */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', fontWeight: 600 }}>
                Basic Information
              </Typography>
              
              {/* Title */}
              <Box sx={{ mb: 3 }}>
                <TextField
                  fullWidth
                  name="title"
                  label="Expense Title"
                  value={formik.values.title}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.title && Boolean(formik.errors.title)}
                  helperText={formik.touched.title && formik.errors.title}
                  sx={{ width: '100%' }}
                />
              </Box>

              {/* Description */}
              <Box sx={{ mb: 3 }}>
                <TextField
                  fullWidth
                  name="description"
                  label="Description"
                  multiline
                  rows={3}
                  value={formik.values.description}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  error={formik.touched.description && Boolean(formik.errors.description)}
                  helperText={formik.touched.description && formik.errors.description}
                  placeholder="Add details about this expense..."
                  sx={{ width: '100%' }}
                />
              </Box>
            </Box>

            <Divider sx={{ my: 3 }} />

            {/* Amount Section */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', fontWeight: 600 }}>
                Amount
              </Typography>
              
              {/* Total Amount */}
              <Box sx={{ mb: 3 }}>
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
                  sx={{ width: '100%' }}
                />
              </Box>
            </Box>

            <Divider sx={{ my: 3 }} />

            {/* Members Selection Section */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', fontWeight: 600 }}>
                Select Members
              </Typography>
              {loadingMembers ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                  <CircularProgress />
                </Box>
              ) : (
                <Paper variant="outlined" sx={{ maxHeight: 300, overflow: 'auto' }}>
                  <List>
                    {planMembers.map((member) => {
                      const memberUser = member.user || {
                        id: member.userId,
                        firstName: member.firstName,
                        lastName: member.lastName,
                        fullName: member.fullName,
                        username: member.username,
                        email: member.email,
                        avatar: member.avatar,
                        profilePicture: member.profilePicture
                      };

                      const fullName = [memberUser?.firstName, memberUser?.lastName].filter(Boolean).join(' ');
                      const displayName = memberUser?.fullName || 
                                        fullName || 
                                        memberUser?.username || 
                                        memberUser?.email || 
                                        `User ${memberUser?.id || member?.userId || 'Unknown'}`;

                      return (
                        <ListItem
                          key={member.userId}
                          dense
                          button
                          onClick={() => handleMemberToggle(member.userId)}
                        >
                          <Checkbox
                            edge="start"
                            checked={selectedMembers.includes(member.userId)}
                            tabIndex={-1}
                            disableRipple
                          />
                          <Avatar
                            src={
                              memberUser?.avatar
                                ? usersAPI.getProfilePicture(memberUser.avatar)
                                : resolveProfilePictureUrl(memberUser?.profilePicture) || 'https://via.placeholder.com/150'
                            }
                            sx={{ width: 32, height: 32, mx: 2 }}
                          />
                          <ListItemText 
                            primary={displayName}
                            secondary={memberUser?.username ? `@${memberUser.username}` : ''}
                          />
                        </ListItem>
                      );
                    })}
                  </List>
                </Paper>
              )}
              {!loadingMembers && selectedMembers.length === 0 && (
                <Typography variant="body2" color="error" sx={{ mt: 1 }}>
                  Please select at least one member to split the expense
                </Typography>
              )}
            </Box>

            <Divider sx={{ my: 3 }} />

            {/* Summary Section */}
            <Box sx={{ mb: 4 }}>
              <Typography variant="h6" gutterBottom sx={{ color: 'primary.main', fontWeight: 600 }}>
                Summary
              </Typography>
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
            </Box>
          </Box>
        </Paper>
      </Container>

      {/* Floating Action Button */}
      <Tooltip title="Create Expense" arrow>
        <Fab
          color="primary"
          aria-label="create expense"
          onClick={formik.handleSubmit}
          disabled={formik.isSubmitting || !formik.values.title || !formik.values.description || !formik.values.totalAmount || selectedMembers.length === 0}
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

    </PageContainer>
  );
};

export default CreateSharedExpense; 