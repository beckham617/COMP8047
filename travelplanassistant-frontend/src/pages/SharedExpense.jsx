import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Paper,
  List,
  Fab,
  Tooltip,
  CircularProgress,
  Alert,
  Chip,
  Avatar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Snackbar
} from '@mui/material';
import {
  Add
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';
import { expensesAPI } from '../services/api';

const SharedExpense = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [warningDialogOpen, setWarningDialogOpen] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');

  useEffect(() => {
    const fetchExpenses = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await expensesAPI.getSharedExpensesForPlan(planId);
        
        // Handle different response structures
        let expensesData = [];
        if (Array.isArray(response)) {
          expensesData = response;
        } else if (response && Array.isArray(response.data)) {
          expensesData = response.data;
        } else if (response && Array.isArray(response.expenses)) {
          expensesData = response.expenses;
        } else if (response && typeof response === 'object') {
          // If response is an object but not an array, wrap it in an array
          expensesData = [response];
        }
        
        console.log('API Response:', response);
        console.log('Processed expenses:', expensesData);
        setExpenses(expensesData);
      } catch (err) {
        console.error('Failed to fetch expenses:', err);
        setError('Failed to load expenses');
        setExpenses([]);
      } finally {
        setLoading(false);
      }
    };

    if (planId) {
      fetchExpenses();
    }
  }, [planId]);

  // Polling for expense updates every 10 seconds to reflect new expenses and payment changes
  useEffect(() => {
    if (!planId) return;

    const pollExpenseUpdates = async () => {
      try {
        const response = await expensesAPI.getSharedExpensesForPlan(planId);
        
        // Handle different response structures
        let newExpensesData = [];
        if (Array.isArray(response)) {
          newExpensesData = response;
        } else if (response && Array.isArray(response.data)) {
          newExpensesData = response.data;
        } else if (response && Array.isArray(response.expenses)) {
          newExpensesData = response.expenses;
        } else if (response && typeof response === 'object') {
          newExpensesData = [response];
        }
        
        setExpenses(prevExpenses => {
          // Only update if there are actual changes to avoid unnecessary re-renders
          if (JSON.stringify(prevExpenses) !== JSON.stringify(newExpensesData)) {
            const prevExpenseIds = new Set(prevExpenses.map(e => e.id));
            const newExpenseIds = new Set(newExpensesData.map(e => e.id));
            
            // Check for new expenses
            const addedExpenses = newExpensesData.filter(expense => !prevExpenseIds.has(expense.id));
            
            // Check for updated expenses (payment status changes)
            const updatedExpenses = newExpensesData.filter(expense => {
              const prevExpense = prevExpenses.find(e => e.id === expense.id);
              return prevExpense && JSON.stringify(prevExpense.participants) !== JSON.stringify(expense.participants);
            });
            
            if (addedExpenses.length > 0) {
              console.log('SharedExpense: New expenses detected', { 
                added: addedExpenses.length, 
                expensePurposes: addedExpenses.map(e => e.purpose) 
              });
              
              // Show notification for new expenses
              if (addedExpenses.length === 1) {
                setSnackbarMessage(`New expense: "${addedExpenses[0].purpose || 'Shared Expense'}"`);
              } else {
                setSnackbarMessage(`${addedExpenses.length} new expenses added!`);
              }
              setSnackbarSeverity('success');
              setSnackbarOpen(true);
            }
            
            if (updatedExpenses.length > 0) {
              console.log('SharedExpense: Payment status updated', { 
                updated: updatedExpenses.length, 
                expensePurposes: updatedExpenses.map(e => e.purpose) 
              });
            }
            
            return newExpensesData;
          }
          return prevExpenses;
        });
      } catch (err) {
        console.error('Failed to poll expense updates:', err);
      }
    };

    // Initial poll after a short delay
    const initialTimeout = setTimeout(pollExpenseUpdates, 4000);

    // Set up polling interval
    const pollInterval = setInterval(pollExpenseUpdates, 10000); // Poll every 10 seconds

    return () => {
      clearTimeout(initialTimeout);
      clearInterval(pollInterval);
    };
  }, [planId]);

  const calculatePerPerson = (totalAmount, personCount) => {
    if (personCount <= 0) return 0;
    return totalAmount / personCount;
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-CA', {
      style: 'currency',
      currency: 'CAD'
    }).format(amount);
  };

  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const handleCreateExpenseClick = () => {
    setWarningDialogOpen(true);
  };

  const handleConfirmCreate = () => {
    setWarningDialogOpen(false);
    navigate(`/create-expense/${planId}`);
  };

  const handleCancelCreate = () => {
    setWarningDialogOpen(false);
  };

  return (
    <PageContainer>
      <PageHeader title="Shared Expenses" onBack={() => navigate(`/plan/${planId}`)}>
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          Track and split travel expenses
        </Typography>
      </PageHeader>

      <Container maxWidth="md" sx={{ mt: -2, py: 6, position: 'relative', zIndex: 1 }}>
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
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
            <CircularProgress />
          </Box>
        ) : error ? (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
         ) : !Array.isArray(expenses) || expenses.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              No expenses yet
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 11 }}>
              Create the first shared expense for this plan!
            </Typography>
          </Box>
        ) : (
          <List>
            {expenses.map((expense, index) => {
              const participantCount = expense.participants ? expense.participants.length : 0;
              const perPerson = calculatePerPerson(expense.totalAmount, participantCount);

              return (
                <Paper key={expense.id || index} elevation={2} sx={{ mb: 3, p: 3, borderRadius: 2 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Box sx={{ flexGrow: 1 }}>
                      <Typography variant="h6" sx={{ mb: 1 }}>
                        {expense.purpose || 'Shared Expense'}
                      </Typography>
                      {expense.description && (
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                          {expense.description}
                        </Typography>
                      )}
                    </Box>
                    <Typography variant="h6" color="primary" sx={{ fontWeight: 'bold' }}>
                      {formatCurrency(expense.totalAmount)}
                    </Typography>
                  </Box>

                   <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                     <Typography variant="body2" color="text.secondary">
                       Split between {participantCount} people
                     </Typography>
                     <Typography variant="body2" color="primary" sx={{ fontWeight: 'bold' }}>
                       {formatCurrency(perPerson)} each
                     </Typography>
                   </Box>

                   {/* Participants */}
                   {expense.participants && expense.participants.length > 0 && (
                     <Box sx={{ mb: 2 }}>
                       <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                         Participants:
                       </Typography>
                       <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                         {expense.participants.map((participant, pIndex) => {
                           const user = participant.user;
                           const displayName = user ? 
                             (user.fullName || `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email || `User ${user.id}`) :
                             `User ${participant.id}`;
                           
                           return (
                             <Chip
                               key={participant.id || pIndex}
                               avatar={
                                 <Avatar 
                                   sx={{ width: 24, height: 24, fontSize: '0.75rem' }}
                                   src={user?.profilePicture}
                                 >
                                   {displayName.charAt(0).toUpperCase()}
                                 </Avatar>
                               }
                               label={`${displayName}: ${formatCurrency(participant.amountOwed)}`}
                               size="small"
                               color={participant.isPaid ? 'success' : 'default'}
                               variant={participant.isPaid ? 'filled' : 'outlined'}
                             />
                           );
                         })}
                       </Box>
                     </Box>
                   )}

                   <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                     {expense.expenseDate && formatDate(expense.expenseDate)}
                     {expense.paidBy && ` • Paid by ${expense.paidBy.fullName || `${expense.paidBy.firstName || ''} ${expense.paidBy.lastName || ''}`.trim() || expense.paidBy.email || `User ${expense.paidBy.id}`}`}
                     {expense.isSettled && ` • Settled`}
                   </Typography>
                </Paper>
              );
            })}
          </List>
        )}
        </Paper>
      </Container>

      {/* Floating Action Button */}
      <Tooltip title="Create Expense" arrow>
        <Fab
          color="primary"
          aria-label="create expense"
          onClick={handleCreateExpenseClick}
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
          <Add />
        </Fab>
      </Tooltip>

      {/* Warning Dialog */}
      <Dialog
        open={warningDialogOpen}
        onClose={handleCancelCreate}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle sx={{ color: 'warning.main', fontWeight: 'bold' }}>
          ⚠️ Payment Confirmation Required
        </DialogTitle>
        <DialogContent>
          <Typography variant="body1" sx={{ mb: 2 }}>
            Before creating a shared expense, please confirm that you have already paid the full amount for the expense you want to split.
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            By creating a shared expense, you are indicating that:
          </Typography>
          <Box component="ul" sx={{ pl: 2, mb: 2 }}>
            <Typography component="li" variant="body2" sx={{ mb: 1 }}>
              You have already paid the total amount upfront
            </Typography>
            <Typography component="li" variant="body2" sx={{ mb: 1 }}>
              Other participants will reimburse you for their share
            </Typography>
            <Typography component="li" variant="body2">
              You are responsible for tracking payments from other members
            </Typography>
          </Box>
          <Typography variant="body2" color="warning.main" sx={{ fontWeight: 'bold' }}>
            Please ensure you have the receipt or proof of payment before proceeding.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ p: 3, pt: 1 }}>
          <Button
            onClick={handleCancelCreate}
            variant="outlined"
            color="inherit"
          >
            Cancel
          </Button>
          <Button
            onClick={handleConfirmCreate}
            variant="contained"
            color="primary"
            sx={{ backgroundColor: '#f43d65', '&:hover': { backgroundColor: '#f297ab' } }}
          >
            I Have Paid - Continue
          </Button>
        </DialogActions>
      </Dialog>

      {/* Notification Snackbar */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={4000}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert 
          onClose={() => setSnackbarOpen(false)} 
          severity={snackbarSeverity}
          sx={{ width: '100%' }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </PageContainer>
  );
};

export default SharedExpense; 