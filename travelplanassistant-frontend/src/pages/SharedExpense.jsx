import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Paper,
  Button,
  IconButton,
  List,
  ListItem,
  ListItemText,
  Divider,
  Fab
} from '@mui/material';
import {
  ArrowBack,
  Add
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const SharedExpense = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [expenses, setExpenses] = useState([]);

  useEffect(() => {
    // Load expenses from localStorage
    const expensesKey = `expenses_${planId}`;
    const storedExpenses = JSON.parse(localStorage.getItem(expensesKey) || '[]');
    setExpenses(storedExpenses);
  }, [planId]);

  const calculatePerPerson = (totalAmount, personCount) => {
    if (personCount <= 0) return 0;
    return totalAmount / personCount;
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
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
              Shared Expenses
            </Typography>
          </Box>
        </Container>
      </Box>

      {/* Expenses List */}
      <Container maxWidth="md" sx={{ py: 3 }}>
        {expenses.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              No expenses yet
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Add the first shared expense for this plan!
            </Typography>
            <Button
              variant="contained"
              startIcon={<Add />}
              onClick={() => navigate(`/create-expense/${planId}`)}
              sx={{
                backgroundColor: '#1976d2',
                '&:hover': {
                  backgroundColor: '#1565c0',
                }
              }}
            >
              Add Expense
            </Button>
          </Box>
        ) : (
          <List>
            {expenses.map((expense, index) => {
              const perPerson = calculatePerPerson(expense.totalAmount, expense.personCount);

              return (
                <Paper key={expense.id} elevation={2} sx={{ mb: 3, p: 3, borderRadius: 2 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Typography variant="h6" sx={{ flexGrow: 1 }}>
                      {expense.title}
                    </Typography>
                    <Typography variant="h6" color="primary" sx={{ fontWeight: 'bold' }}>
                      {formatCurrency(expense.totalAmount)}
                    </Typography>
                  </Box>

                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                    <Typography variant="body2" color="text.secondary">
                      Split between {expense.personCount} people
                    </Typography>
                    <Typography variant="body2" color="primary" sx={{ fontWeight: 'bold' }}>
                      {formatCurrency(perPerson)} each
                    </Typography>
                  </Box>

                  <Typography variant="caption" color="text.secondary" sx={{ display: 'block' }}>
                    Added by {expense.createdBy} • {formatDate(expense.createdAt)}
                  </Typography>
                </Paper>
              );
            })}
          </List>
        )}
      </Container>

      {/* Floating Action Button */}
      <Fab
        color="primary"
        aria-label="add expense"
        onClick={() => navigate(`/create-expense/${planId}`)}
        sx={{
          position: 'fixed',
          bottom: 80,
          right: 16,
          backgroundColor: '#1976d2',
          '&:hover': {
            backgroundColor: '#1565c0',
          }
        }}
      >
        <Add />
      </Fab>
    </Box>
  );
};

export default SharedExpense; 