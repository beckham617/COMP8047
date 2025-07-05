import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Paper,
  Button,
  Avatar,
  Chip,
  Grid,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  Divider,
  Card,
  CardContent,
  CardMedia,
  Fab
} from '@mui/material';
import {
  ArrowBack,
  Check,
  Close,
  Chat,
  Poll,
  AttachMoney,
  PersonAdd
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const PlanDetails = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [plan, setPlan] = useState(null);
  const [userPlan, setUserPlan] = useState(null);
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteError, setInviteError] = useState('');
  const [inviteSuccess, setInviteSuccess] = useState('');

  useEffect(() => {
    // Load plan from localStorage
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const foundPlan = plans.find(p => p.id === planId);
    if (foundPlan) {
      setPlan(foundPlan);
      const userPlanData = foundPlan.members.find(m => m.userId === user?.id);
      setUserPlan(userPlanData);
    }
  }, [planId, user]);

  const handleApply = () => {
    if (!plan || !user) return;

    const updatedPlan = {
      ...plan,
      members: [
        ...plan.members,
        {
          userId: user.id,
          userPlanStatus: 'applied',
          joinedAt: new Date().toISOString()
        }
      ]
    };

    // Update plans in localStorage
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const updatedPlans = plans.map(p => p.id === planId ? updatedPlan : p);
    localStorage.setItem('plans', JSON.stringify(updatedPlans));

    setPlan(updatedPlan);
    setUserPlan({ userId: user.id, userPlanStatus: 'applied' });
    navigate('/my-plans');
  };

  const handleCancelApplication = () => {
    if (!plan || !user) return;

    const updatedPlan = {
      ...plan,
      members: plan.members.map(m => 
        m.userId === user.id 
          ? { ...m, userPlanStatus: 'applied_cancelled' }
          : m
      )
    };

    // Update plans in localStorage
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const updatedPlans = plans.map(p => p.id === planId ? updatedPlan : p);
    localStorage.setItem('plans', JSON.stringify(updatedPlans));

    setPlan(updatedPlan);
    setUserPlan({ userId: user.id, userPlanStatus: 'applied_cancelled' });
    navigate('/my-plans');
  };

  const handleAcceptInvitation = () => {
    if (!plan || !user) return;

    const updatedPlan = {
      ...plan,
      members: plan.members.map(m => 
        m.userId === user.id 
          ? { ...m, userPlanStatus: 'invited_accepted' }
          : m
      )
    };

    // Update plans in localStorage
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const updatedPlans = plans.map(p => p.id === planId ? updatedPlan : p);
    localStorage.setItem('plans', JSON.stringify(updatedPlans));

    setPlan(updatedPlan);
    setUserPlan({ userId: user.id, userPlanStatus: 'invited_accepted' });
  };

  const handleRefuseInvitation = () => {
    if (!plan || !user) return;

    const updatedPlan = {
      ...plan,
      members: plan.members.map(m => 
        m.userId === user.id 
          ? { ...m, userPlanStatus: 'invited_refused' }
          : m
      )
    };

    // Update plans in localStorage
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const updatedPlans = plans.map(p => p.id === planId ? updatedPlan : p);
    localStorage.setItem('plans', JSON.stringify(updatedPlans));

    setPlan(updatedPlan);
    setUserPlan({ userId: user.id, userPlanStatus: 'invited_refused' });
    navigate('/my-plans');
  };

  const handleAcceptApplication = (memberId) => {
    if (!plan) return;

    const updatedPlan = {
      ...plan,
      members: plan.members.map(m => 
        m.userId === memberId 
          ? { ...m, userPlanStatus: 'applied_accepted' }
          : m
      )
    };

    // Update plans in localStorage
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const updatedPlans = plans.map(p => p.id === planId ? updatedPlan : p);
    localStorage.setItem('plans', JSON.stringify(updatedPlans));

    setPlan(updatedPlan);
  };

  const handleRefuseApplication = (memberId) => {
    if (!plan) return;

    const updatedPlan = {
      ...plan,
      members: plan.members.map(m => 
        m.userId === memberId 
          ? { ...m, userPlanStatus: 'applied_refused' }
          : m
      )
    };

    // Update plans in localStorage
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const updatedPlans = plans.map(p => p.id === planId ? updatedPlan : p);
    localStorage.setItem('plans', JSON.stringify(updatedPlans));

    setPlan(updatedPlan);
  };

  const handleInvite = () => {
    setInviteDialogOpen(true);
  };

  const handleSendInvitation = () => {
    if (!inviteEmail || !plan) return;

    // Validate email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(inviteEmail)) {
      setInviteError('Please enter a valid email address');
      return;
    }

    // Check if user already has a current plan
    const users = JSON.parse(localStorage.getItem('users') || '[]');
    const invitee = users.find(u => u.email === inviteEmail);
    
    if (!invitee) {
      setInviteError('User not found with this email address');
      return;
    }

    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const hasCurrentPlan = plans.some(p => 
      p.members.some(m => 
        m.userId === invitee.id && 
        ['new', 'in_progress'].includes(p.planStatus) &&
        ['owned', 'applied', 'applied_accepted', 'invited', 'invited_accepted'].includes(m.userPlanStatus)
      )
    );

    if (hasCurrentPlan) {
      setInviteError('This user already has a plan in their Current section');
      return;
    }

    // Add invitation
    const updatedPlan = {
      ...plan,
      members: [
        ...plan.members,
        {
          userId: invitee.id,
          userPlanStatus: 'invited',
          joinedAt: new Date().toISOString()
        }
      ]
    };

    // Update plans in localStorage
    const updatedPlans = plans.map(p => p.id === planId ? updatedPlan : p);
    localStorage.setItem('plans', JSON.stringify(updatedPlans));

    setPlan(updatedPlan);
    setInviteSuccess('Invitation sent successfully!');
    setInviteEmail('');
    setInviteError('');
    
    setTimeout(() => {
      setInviteDialogOpen(false);
      setInviteSuccess('');
    }, 2000);
  };

  const handleCloseInviteDialog = () => {
    setInviteDialogOpen(false);
    setInviteEmail('');
    setInviteError('');
    setInviteSuccess('');
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'new':
        return 'success';
      case 'in_progress':
        return 'warning';
      case 'completed':
        return 'info';
      case 'cancelled':
        return 'error';
      default:
        return 'default';
    }
  };

  const getUserPlanStatusColor = (status) => {
    switch (status) {
      case 'owned':
        return 'primary';
      case 'applied':
        return 'warning';
      case 'applied_accepted':
        return 'success';
      case 'applied_refused':
        return 'error';
      case 'invited':
        return 'info';
      case 'invited_accepted':
        return 'success';
      case 'invited_refused':
        return 'error';
      default:
        return 'default';
    }
  };

  const canApply = () => {
    if (!plan || !user) return false;
    
    // Check if user already has a current plan
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const hasCurrentPlan = plans.some(p => 
      p.members.some(m => 
        m.userId === user.id && 
        ['new', 'in_progress'].includes(p.planStatus) &&
        ['owned', 'applied', 'applied_accepted', 'invited', 'invited_accepted'].includes(m.userPlanStatus)
      )
    );

    return !hasCurrentPlan && plan.planStatus === 'new' && plan.visibility === 'PUBLIC';
  };

  const isOwner = userPlan?.userPlanStatus === 'owned';
  const isApplied = userPlan?.userPlanStatus === 'applied';
  const isInvited = userPlan?.userPlanStatus === 'invited';
  const isAccepted = ['applied_accepted', 'invited_accepted'].includes(userPlan?.userPlanStatus);

  if (!plan) {
    return (
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="h6" color="text.secondary">
          Plan not found
        </Typography>
      </Box>
    );
  }

  const activeMembers = plan.members.filter(m => 
    ['applied', 'applied_accepted', 'invited', 'invited_accepted'].includes(m.userPlanStatus)
  );

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
              Plan Details
            </Typography>
          </Box>
        </Container>
      </Box>

      {/* Plan Content */}
      <Container maxWidth="lg" sx={{ py: 3 }}>
        <Grid container spacing={3}>
          {/* Plan Info */}
          <Grid item xs={12} md={8}>
            <Paper elevation={3} sx={{ p: 3, borderRadius: 3, mb: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Typography variant="h4" component="h2" sx={{ flexGrow: 1 }}>
                  {plan.title}
                </Typography>
                <Chip
                  label={plan.planStatus.replace('_', ' ')}
                  color={getStatusColor(plan.planStatus)}
                />
              </Box>

              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <Avatar
                  src={plan.owner.avatar}
                  sx={{ width: 40, height: 40, mr: 2 }}
                />
                <Typography 
                  variant="body1" 
                  sx={{ cursor: 'pointer', color: '#1976d2' }}
                  onClick={() => navigate(`/user/${plan.owner.id}`)}
                >
                  {plan.owner.firstName} {plan.owner.lastName}
                </Typography>
              </Box>

              <Typography variant="body1" sx={{ mb: 3 }}>
                {plan.description}
              </Typography>

              {plan.images && plan.images.length > 0 && (
                <Box sx={{ mb: 3 }}>
                  <Typography variant="h6" gutterBottom>
                    Photos
                  </Typography>
                  <Grid container spacing={2}>
                    {plan.images.map((image, index) => (
                      <Grid item xs={6} sm={4} key={index}>
                        <Card>
                          <CardMedia
                            component="img"
                            height="140"
                            image={image}
                            alt={`Plan image ${index + 1}`}
                          />
                        </Card>
                      </Grid>
                    ))}
                  </Grid>
                </Box>
              )}
            </Paper>
          </Grid>

          {/* Action Buttons */}
          <Grid item xs={12} md={4}>
            <Paper elevation={3} sx={{ p: 3, borderRadius: 3, mb: 3 }}>
              <Typography variant="h6" gutterBottom>
                Actions
              </Typography>

              {canApply() && (
                <Button
                  fullWidth
                  variant="contained"
                  onClick={handleApply}
                  sx={{ mb: 2 }}
                >
                  Apply
                </Button>
              )}

              {isApplied && (
                <Button
                  fullWidth
                  variant="outlined"
                  color="error"
                  onClick={handleCancelApplication}
                  sx={{ mb: 2 }}
                >
                  Cancel Application
                </Button>
              )}

              {isInvited && (
                <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                  <Button
                    fullWidth
                    variant="contained"
                    color="success"
                    startIcon={<Check />}
                    onClick={handleAcceptInvitation}
                  >
                    Accept
                  </Button>
                  <Button
                    fullWidth
                    variant="outlined"
                    color="error"
                    startIcon={<Close />}
                    onClick={handleRefuseInvitation}
                  >
                    Refuse
                  </Button>
                </Box>
              )}

              {plan.planStatus === 'in_progress' && isAccepted && (
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                  <Button
                    fullWidth
                    variant="contained"
                    startIcon={<Chat />}
                    onClick={() => navigate(`/chat/${planId}`)}
                  >
                    Chat Room
                  </Button>
                  <Button
                    fullWidth
                    variant="contained"
                    startIcon={<Poll />}
                    onClick={() => navigate(`/poll/${planId}`)}
                  >
                    Poll
                  </Button>
                  <Button
                    fullWidth
                    variant="contained"
                    startIcon={<AttachMoney />}
                    onClick={() => navigate(`/expense/${planId}`)}
                  >
                    Shared Expense
                  </Button>
                </Box>
              )}
            </Paper>
          </Grid>

          {/* Members List */}
          <Grid item xs={12}>
            <Paper elevation={3} sx={{ p: 3, borderRadius: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h6">
                  Plan Members ({activeMembers.filter(m => ['applied_accepted', 'invited_accepted'].includes(m.userPlanStatus)).length}/{activeMembers.length})
                </Typography>
                {isOwner && (
                  <Button
                    variant="outlined"
                    startIcon={<PersonAdd />}
                    onClick={handleInvite}
                  >
                    Invite
                  </Button>
                )}
              </Box>

              <List>
                {activeMembers.map((member) => {
                  const memberUser = JSON.parse(localStorage.getItem('users') || '[]')
                    .find(u => u.id === member.userId);
                  
                  if (!memberUser) return null;

                  return (
                    <ListItem key={member.userId} divider>
                      <ListItemAvatar>
                        <Avatar src={memberUser.avatar} />
                      </ListItemAvatar>
                      <ListItemText
                        primary={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Typography
                              variant="body1"
                              sx={{ cursor: 'pointer', color: '#1976d2' }}
                              onClick={() => navigate(`/user/${memberUser.id}`)}
                            >
                              {memberUser.firstName} {memberUser.lastName}
                            </Typography>
                            <Chip
                              label={member.userPlanStatus.replace('_', ' ')}
                              color={getUserPlanStatusColor(member.userPlanStatus)}
                              size="small"
                            />
                          </Box>
                        }
                        secondary={`@${memberUser.username}`}
                      />
                      {isOwner && member.userPlanStatus === 'applied' && (
                        <ListItemSecondaryAction>
                          <IconButton
                            color="success"
                            onClick={() => handleAcceptApplication(member.userId)}
                            sx={{ mr: 1 }}
                          >
                            <Check />
                          </IconButton>
                          <IconButton
                            color="error"
                            onClick={() => handleRefuseApplication(member.userId)}
                          >
                            <Close />
                          </IconButton>
                        </ListItemSecondaryAction>
                      )}
                    </ListItem>
                  );
                })}
              </List>
            </Paper>
          </Grid>
        </Grid>
      </Container>

      {/* Invite Dialog */}
      <Dialog open={inviteDialogOpen} onClose={handleCloseInviteDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Invite Member</DialogTitle>
        <DialogContent>
          {inviteError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {inviteError}
            </Alert>
          )}
          {inviteSuccess && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {inviteSuccess}
            </Alert>
          )}
          <TextField
            fullWidth
            label="Email Address"
            type="email"
            value={inviteEmail}
            onChange={(e) => setInviteEmail(e.target.value)}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseInviteDialog}>Cancel</Button>
          <Button onClick={handleSendInvitation} variant="contained">
            Send Invitation
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default PlanDetails; 