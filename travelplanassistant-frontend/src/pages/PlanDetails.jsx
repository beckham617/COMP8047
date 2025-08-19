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
import { travelPlansAPI, usersAPI } from '../services/api';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';

const PlanDetails = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
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
  const [plan, setPlan] = useState(null);
  const [userPlan, setUserPlan] = useState(null);
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteError, setInviteError] = useState('');
  const [inviteSuccess, setInviteSuccess] = useState('');

  useEffect(() => {
    const loadPlan = async () => {
      try {
        const fetchedPlan = await travelPlansAPI.getPlanDetails(planId);
        setPlan(fetchedPlan);
        const userPlanData = fetchedPlan.members?.find(m => m.userId === user?.id) || null;
        setUserPlan(userPlanData);
      } catch (err) {
        console.error('Failed to load plan details:', err);
        setPlan(null);
        setUserPlan(null);
      }
    };
    if (planId) {
      loadPlan();
    }
  }, [planId, user]);

  const handleApply = () => {
    if (!plan || !user) return;

    const currentMembers = Array.isArray(plan.members) ? plan.members : [];
    const updatedPlan = {
      ...plan,
      members: [
        ...currentMembers,
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

    const currentMembers = Array.isArray(plan.members) ? plan.members : [];
    const updatedPlan = {
      ...plan,
      members: currentMembers.map(m => 
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

    const currentMembers = Array.isArray(plan.members) ? plan.members : [];
    const updatedPlan = {
      ...plan,
      members: currentMembers.map(m => 
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

    const currentMembers = Array.isArray(plan.members) ? plan.members : [];
    const updatedPlan = {
      ...plan,
      members: currentMembers.map(m => 
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

    const currentMembers = Array.isArray(plan.members) ? plan.members : [];
    const updatedPlan = {
      ...plan,
      members: currentMembers.map(m => 
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

    const currentMembers = Array.isArray(plan.members) ? plan.members : [];
    const updatedPlan = {
      ...plan,
      members: currentMembers.map(m => 
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
    const s = (status || '').toString().toLowerCase();
    switch (s) {
      case 'new':
        return 'success';
      case 'in_progress':
      case 'in-progress':
        return 'warning';
      case 'completed':
        return 'info';
      case 'cancelled':
      case 'canceled':
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

    // Check if user already has a current plan (local fallback)
    const plans = JSON.parse(localStorage.getItem('plans') || '[]');
    const hasCurrentPlan = plans.some(p => {
      const pStatus = (p.planStatus || p.status || '').toString().toLowerCase();
      const pMembers = Array.isArray(p.members) ? p.members : [];
      return pMembers.some(m =>
        m.userId === user.id &&
        ['new', 'in_progress'].includes(pStatus) &&
        ['owned', 'applied', 'applied_accepted', 'invited', 'invited_accepted'].includes(m.userPlanStatus)
      );
    });

    const statusLower = (plan.planStatus || plan.status || '').toString().toLowerCase();
    const visibility = (plan.visibility || plan.planType || '').toString().toUpperCase();

    return !hasCurrentPlan && statusLower === 'new' && visibility === 'PUBLIC';
  };

  const isOwner = userPlan?.userPlanStatus === 'owned';
  const isApplied = userPlan?.userPlanStatus === 'applied';
  const isInvited = userPlan?.userPlanStatus === 'invited';
  const isAccepted = ['applied_accepted', 'invited_accepted'].includes(userPlan?.userPlanStatus);

  const handleBack = () => {
    navigate(-1);
  };

  if (!plan) {
    return (
      <PageContainer>
        <PageHeader title="Plan Details" onBack={handleBack} />
        <Container maxWidth="sm" sx={{ position: 'relative', zIndex: 1, py: 4 }}>
          <Paper elevation={8} sx={{ p: 4, borderRadius: 3, backgroundColor: 'transparent' }}>
            <Box sx={{ textAlign: 'center' }}>
              <Typography variant="h6" color="text.secondary">
                Plan not found
              </Typography>
            </Box>
          </Paper>
        </Container>
      </PageContainer>
    );
  }

  const memberList = Array.isArray(plan.members) ? plan.members : [];
  const activeMembers = memberList.filter(m => 
    ['owned', 'applied', 'applied_accepted', 'invited', 'invited_accepted'].includes(m.userPlanStatus)
  );

  return (
    <PageContainer>
      <PageHeader title="Plan Details" onBack={handleBack}>
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          View and manage plan details
        </Typography>
      </PageHeader>

      {/* Plan Content */}
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
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            {/* Plan Title and Status */}
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
              <Typography variant="h4" component="h2" sx={{ flexGrow: 1, mr: 2 }}>
                {plan.title}
              </Typography>
              <Chip
                label={(plan.planStatus ? String(plan.planStatus).replace('_', ' ') : (plan.status ? String(plan.status).replace('_', ' ') : 'New'))}
                color={getStatusColor(plan.planStatus || plan.status)}
                size="medium"
              />
            </Box>

            {/* Plan Owner */}
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
              <Avatar
                src={
                  plan.ownerAvatar
                    ? usersAPI.getProfilePicture(plan.ownerAvatar)
                    : (plan.ownerAvatar || resolveProfilePictureUrl(plan.owner?.profilePicture) || 'https://via.placeholder.com/150')
                }
                sx={{ width: 50, height: 50, mr: 2 }}
              />
              <Box>
                <Typography 
                  variant="h6" 
                  sx={{ cursor: 'pointer', color: '#1976d2' }}
                  onClick={() => navigate(`/user/${plan.owner?.id}`)}
                >
                  {plan.ownerName || [plan.owner?.firstName, plan.owner?.lastName].filter(Boolean).join(' ')}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Plan Owner
                </Typography>
              </Box>
            </Box>

            {/* Plan Details Section */}
            <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>Plan Details</Typography>
            
            {/* Plan Type */}
            {plan.planType && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Plan Type</Typography>
                <Typography variant="body1">{plan.planType}</Typography>
              </Box>
            )}

            {/* Category */}
            {plan.category && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Category</Typography>
                <Typography variant="body1">{plan.category}</Typography>
              </Box>
            )}

            {/* Dates */}
            {(plan.startDate || plan.endDate) && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Duration</Typography>
                <Typography variant="body1">
                  {plan.startDate && new Date(plan.startDate).toLocaleDateString()}
                  {plan.startDate && plan.endDate && ' - '}
                  {plan.endDate && new Date(plan.endDate).toLocaleDateString()}
                </Typography>
              </Box>
            )}

            {/* Origin and Destination */}
            {((plan.originCountry || plan.destinationCountry) || (plan.originCity || plan.destinationCity)) && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Route</Typography>
                <Typography variant="body1">
                  {plan.originCity && `${plan.originCity}, `}{plan.originCountry || ''}
                  {((plan.originCountry || plan.originCity) && (plan.destinationCountry || plan.destinationCity)) && ' → '}
                  {plan.destinationCity && `${plan.destinationCity}, `}{plan.destinationCountry || ''}
                </Typography>
              </Box>
            )}

            {/* Transportation */}
            {(plan.transportation || plan.transportationType) && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Transportation</Typography>
                <Typography variant="body1">{plan.transportation || plan.transportationType}</Typography>
              </Box>
            )}

            {/* Accommodation */}
            {(plan.accommodation || plan.accommodationType) && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Accommodation</Typography>
                <Typography variant="body1">{plan.accommodation || plan.accommodationType}</Typography>
              </Box>
            )}

            {/* Max Members */}
            {typeof plan.maxMembers !== 'undefined' && plan.maxMembers !== null && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Maximum Members</Typography>
                <Typography variant="body1">{plan.maxMembers}</Typography>
              </Box>
            )}

            {/* Description */}
            {plan.description && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Description</Typography>
                <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
                  {plan.description}
                </Typography>
              </Box>
            )}

            {/* Plan Images */}
            {plan.images && plan.images.length > 0 && (
              <Box sx={{ mb: 3 }}>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>Photos</Typography>
                <Grid container spacing={2}>
                  {plan.images.map((image, index) => (
                    <Grid item xs={6} sm={4} key={index}>
                      <Card>
                        <CardMedia
                          component="img"
                          height="120"
                          image={resolveProfilePictureUrl(image) || image}
                          alt={`Plan image ${index + 1}`}
                          sx={{ objectFit: 'cover' }}
                        />
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              </Box>
            )}

            {/* Restrictions Section */}
            <Typography variant="h6" sx={{ mt: 4, mb: 2, fontWeight: 'bold' }}>Restrictions</Typography>
            
            {/* Gender */}
            {plan.gender && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Gender</Typography>
                <Typography variant="body1">{plan.gender === 'ANY' ? 'Any' : plan.gender}</Typography>
              </Box>
            )}

            {/* Age Range */}
            {(typeof plan.ageMin !== 'undefined' || typeof plan.ageMax !== 'undefined') && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Age Range</Typography>
                <Typography variant="body1">
                  {plan.ageMin && plan.ageMax ? `${plan.ageMin} - ${plan.ageMax} years` : 
                   plan.ageMin ? `${plan.ageMin}+ years` : 
                   plan.ageMax ? `Up to ${plan.ageMax} years` : 'Any age'}
                </Typography>
              </Box>
            )}

            {/* Language */}
            {plan.language && (
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="text.secondary">Language</Typography>
                <Typography variant="body1">{plan.language}</Typography>
              </Box>
            )}

            {/* Action Buttons */}
            <Box sx={{ mt: 4 }}>
              <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>Actions</Typography>

              {canApply() && (
                <Button
                  fullWidth
                  variant="contained"
                  onClick={handleApply}
                  sx={{ mb: 2, backgroundColor: '#f43d65', '&:hover': { backgroundColor: '#f297ab' } }}
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
                    sx={{ backgroundColor: '#f43d65', '&:hover': { backgroundColor: '#f297ab' } }}
                  >
                    Chat Room
                  </Button>
                  <Button
                    fullWidth
                    variant="contained"
                    startIcon={<Poll />}
                    onClick={() => navigate(`/poll/${planId}`)}
                    sx={{ backgroundColor: '#f43d65', '&:hover': { backgroundColor: '#f297ab' } }}
                  >
                    Poll
                  </Button>
                  <Button
                    fullWidth
                    variant="contained"
                    startIcon={<AttachMoney />}
                    onClick={() => navigate(`/expense/${planId}`)}
                    sx={{ backgroundColor: '#f43d65', '&:hover': { backgroundColor: '#f297ab' } }}
                  >
                    Shared Expense
                  </Button>
                </Box>
              )}
            </Box>

            {/* Members Section */}
            <Box sx={{ mt: 4 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  Plan Members ({activeMembers.filter(m => ['applied_accepted', 'invited_accepted'].includes(m.userPlanStatus)).length}/{plan.maxMembers || activeMembers.length})
                </Typography>
                {isOwner && (
                  <Button
                    variant="outlined"
                    startIcon={<PersonAdd />}
                    onClick={handleInvite}
                    size="small"
                  >
                    Invite
                  </Button>
                )}
              </Box>

              <Paper variant="outlined" sx={{ backgroundColor: 'rgba(255, 255, 255, 0.7)' }}>
                <List>
                  {activeMembers.map((member) => {
                    const memberUser = JSON.parse(localStorage.getItem('users') || '[]')
                      .find(u => u.id === member.userId);
                    
                    if (!memberUser) return null;

                    return (
                      <ListItem key={member.userId} divider>
                        <ListItemAvatar>
                          <Avatar
                            src={
                              memberUser.avatar
                                ? usersAPI.getProfilePicture(memberUser.avatar)
                                : (memberUser.avatar || resolveProfilePictureUrl(memberUser.profilePicture) || 'https://via.placeholder.com/150')
                            }
                            sx={{ width: 32, height: 32 }}
                          />
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
                                label={(member.userPlanStatus ? String(member.userPlanStatus).replace('_', ' ') : 'member')}
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
            </Box>
          </Box>
        </Paper>
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
    </PageContainer>
  );
};

export default PlanDetails; 