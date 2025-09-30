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
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  Card,
  CardMedia,
  Fab,
  Tooltip,
  Snackbar
} from '@mui/material';
import {
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
import UserInfoDialog from '../components/UserInfoDialog';

const PlanDetails = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  // Mapping function to convert backend categories to user-friendly display format
  const mapCategoryToDisplay = (backendCategory) => {
    const categoryMap = {
      'TRIP': 'Trip',
      'SPORTS': 'Sports',
      'GAME': 'Game',
      'MATCH': 'Match',
      'EVENT': 'Event',
      'CONCERT': 'Concert',
      'SHOW': 'Show',
      'FAMILY_TIME': 'Family time'
    };
    return categoryMap[backendCategory] || backendCategory.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
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
  const [plan, setPlan] = useState(null);
  const [userPlan, setUserPlan] = useState(null);
  const [inviteDialogOpen, setInviteDialogOpen] = useState(false);
  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteError, setInviteError] = useState('');
  const [inviteSuccess, setInviteSuccess] = useState('');
  const [isInviting, setIsInviting] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('error');
  const [closeDialogOpen, setCloseDialogOpen] = useState(false);
  const [closeReason, setCloseReason] = useState('');
  const [userInfoDialogOpen, setUserInfoDialogOpen] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [previousMemberCount, setPreviousMemberCount] = useState(0);

  useEffect(() => {
    const loadPlan = async () => {
      try {
        const fetchedPlan = await travelPlansAPI.getPlanDetails(planId);
        setPlan(fetchedPlan);
        const userPlanData = fetchedPlan.members?.find(m => m.userId === user?.id) || null;
        setUserPlan(userPlanData);
        
        // Initialize member count for polling comparison
        const initialMemberCount = fetchedPlan.members?.filter(m => 
          ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'].includes((m.userPlanStatus || '').toString().toUpperCase())
        ).length || 0;
        setPreviousMemberCount(initialMemberCount);
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

  // Polling for plan updates every 10 seconds to reflect member list changes
  useEffect(() => {
    if (!planId || !user) return;

    const pollPlanUpdates = async () => {
      try {
        const refreshed = await travelPlansAPI.getPlanDetails(planId);
        setPlan(prevPlan => {
          // Only update if there are actual changes to avoid unnecessary re-renders
          if (!prevPlan || 
              JSON.stringify(prevPlan.members) !== JSON.stringify(refreshed.members) ||
              prevPlan.userPlanStatus !== refreshed.userPlanStatus ||
              prevPlan.status !== refreshed.status) {
            
            // Calculate member count changes
            const currentMemberCount = refreshed.members?.filter(m => 
              ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'].includes((m.userPlanStatus || '').toString().toUpperCase())
            ).length || 0;
            
            // Show notification if member count changed
            if (previousMemberCount > 0 && currentMemberCount !== previousMemberCount) {
              const change = currentMemberCount - previousMemberCount;
              if (change > 0) {
                setSnackbarMessage(`Member list updated: ${change} new member${change > 1 ? 's' : ''} joined!`);
                setSnackbarSeverity('success');
                setSnackbarOpen(true);
              } else if (change < 0) {
                setSnackbarMessage(`Member list updated: ${Math.abs(change)} member${Math.abs(change) > 1 ? 's' : ''} left`);
                setSnackbarSeverity('info');
                setSnackbarOpen(true);
              }
            }
            
            setPreviousMemberCount(currentMemberCount);
            
            return refreshed;
          }
          return prevPlan;
        });
        
        // Update user plan data
        const userPlanData = refreshed.members?.find(m => m.userId === user.id) || null;
        setUserPlan(userPlanData);
      } catch (err) {
        console.error('Failed to poll plan updates:', err);
      }
    };

    // Initial poll after a short delay
    const initialTimeout = setTimeout(pollPlanUpdates, 3000);

    // Set up polling interval
    const pollInterval = setInterval(pollPlanUpdates, 10000); // Poll every 10 seconds

    return () => {
      clearTimeout(initialTimeout);
      clearInterval(pollInterval);
    };
  }, [planId, user]);

  const handleApply = async () => {
    if (!plan || !user) return;
    try {
      await travelPlansAPI.applyToPlan(planId);
      // Refresh plan after applying
      const refreshed = await travelPlansAPI.getPlanDetails(planId);
      setPlan(refreshed);
      const userPlanData = refreshed.members?.find(m => m.userId === user?.id) || null;
      setUserPlan(userPlanData);
    } catch (e) {
      console.error('Apply failed:', e);
      setSnackbarMessage(e.message || 'Failed to apply');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    }
  };

  const handleCancelApplication = async () => {
    if (!plan || !user) return;
    try {
      await travelPlansAPI.cancelApplication(planId);
      const refreshed = await travelPlansAPI.getPlanDetails(planId);
      setPlan(refreshed);
      const userPlanData = refreshed.members?.find(m => m.userId === user?.id) || null;
      setUserPlan(userPlanData);
    } catch (e) {
      console.error('Cancel application failed:', e);
      setSnackbarMessage(e.message || 'Failed to cancel application');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    }
  };

  const handleInvitationDecision = (decision) => {
    if (!plan || !user) return;
    const apiCall = decision === 'accept' ? travelPlansAPI.acceptInvitation : travelPlansAPI.refuseInvitation;
    apiCall(planId)
      .then(async () => {
        try {
          const refreshed = await travelPlansAPI.getPlanDetails(planId);
          setPlan(refreshed);
          const userPlanData = refreshed.members?.find(m => m.userId === user?.id) || null;
          setUserPlan(userPlanData);
          if (decision === 'reject') navigate('/my-plans');
        } catch (e) {
          console.error('Failed to refresh plan after decision:', e);
          if (decision === 'reject') navigate('/my-plans');
        }
      })
      .catch(err => {
        console.error(`Invitation ${decision} failed:`, err);
        setSnackbarMessage(err.message || `Invitation ${decision} failed`);
        setSnackbarSeverity('error');
        setSnackbarOpen(true);
      });
  };

  const handleApplicationDecision = async (memberId, decision) => {
    if (!plan) return;
    try {
      if (decision === 'accept') {
        await travelPlansAPI.acceptApplication(planId, memberId);
      } else {
        await travelPlansAPI.refuseApplication(planId, memberId);
      }
      const refreshed = await travelPlansAPI.getPlanDetails(planId);
      setPlan(refreshed);
    } catch (e) {
      console.error(`Application ${decision} failed:`, e);
      setSnackbarMessage(e.message || `Application ${decision} failed`);
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    }
  };

  const handleClosePlan = async () => {
    if (!plan || !user) return;
    if (!closeReason.trim()) {
      setSnackbarMessage('Please provide a reason for closing the plan');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
      return;
    }
    try {
      await travelPlansAPI.closePlan(planId, closeReason);
      const refreshed = await travelPlansAPI.getPlanDetails(planId);
      setPlan(refreshed);
      setSnackbarMessage('Plan closed successfully');
      setSnackbarSeverity('success');
      setSnackbarOpen(true);
      setCloseDialogOpen(false);
      setCloseReason('');
      navigate('/my-plans');
    } catch (e) {
      console.error('Close plan failed:', e);
      setSnackbarMessage(e.message || 'Failed to close plan');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    }
  };

  const handleOpenCloseDialog = () => {
    setCloseDialogOpen(true);
  };

  const handleCloseCloseDialog = () => {
    setCloseDialogOpen(false);
    setCloseReason('');
  };

  const handleUserClick = (userId) => {
    console.log('handleUserClick called with userId:', userId);
    if (userId) {
      setSelectedUserId(userId);
      setUserInfoDialogOpen(true);
    } else {
      console.warn('No userId provided to handleUserClick');
    }
  };

  const handleCloseUserInfoDialog = () => {
    setUserInfoDialogOpen(false);
    setSelectedUserId(null);
  };

  const handleInvite = () => {
    setInviteDialogOpen(true);
  };

  const handleSendInvitation = async () => {
    if (!inviteEmail || !plan || isInviting) return;

    // Validate email (basic format check; detailed validation handled by backend)
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(inviteEmail)) {
      setInviteError('Please enter a valid email address');
      return;
    }

    try {
      setIsInviting(true);
      setInviteError('');
      setInviteSuccess('');
      await travelPlansAPI.inviteUser(planId, inviteEmail);

      // On success, close the dialog immediately
      setInviteEmail('');
      setInviteDialogOpen(false);

      // Refresh in background (best-effort)
      (async () => {
        try {
          const refreshed = await travelPlansAPI.getPlanDetails(planId);
          setPlan(refreshed);
          const userPlanData = refreshed.members?.find(m => m.userId === user?.id) || null;
          setUserPlan(userPlanData);
        } catch (refreshErr) {
          console.error('Failed to refresh plan after invite:', refreshErr);
        }
      })();
    } catch (apiErr) {
      const message = (apiErr && apiErr.message) ? apiErr.message : 'Failed to send invitation';
      setInviteError(message);
    } finally {
      setIsInviting(false);
    }
  };

  const handleCloseInviteDialog = () => {
    setInviteDialogOpen(false);
    setInviteEmail('');
    setInviteError('');
    setInviteSuccess('');
    setIsInviting(false);
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
        return 'success';
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


  const isOwner = plan?.userPlanStatus === 'OWNED';

  const handleBack = () => {
    const from = window.history.state && window.history.state.usr && window.history.state.usr.from;
    if (from === 'discovery') {
      navigate('/discovery');
    } else {
      navigate('/my-plans');
    }
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
  const acceptedCount = memberList.filter(m => ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'].includes((m.userPlanStatus || '').toString().toUpperCase())).length;
  const isFull = typeof plan.maxMembers === 'number' && acceptedCount >= plan.maxMembers;
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
                  onClick={() => {
                    // Try different possible owner ID fields
                    const ownerId = plan.owner?.id || plan.ownerId || plan.owner?.userId || 
                                   (memberList.find(m => m.userPlanStatus === 'OWNED')?.userId);
                    console.log('Owner ID:', ownerId, 'Plan owner:', plan.owner);
                    handleUserClick(ownerId);
                  }}
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
                <Typography variant="body1">{mapCategoryToDisplay(plan.category)}</Typography>
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

            {/* Actions section removed */}

            {/* Members Section */}
            <Box sx={{ mt: 4 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  Members ({acceptedCount}/{plan.maxMembers && plan.maxMembers > 0 ? plan.maxMembers : '∞'})
                </Typography>
                {isOwner && !isFull && (String(plan.planStatus || plan.status || '').toUpperCase() === 'NEW') && (
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
                  {memberList.map((member) => {
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

                    const statusUpper = (member.userPlanStatus || '').toString().toUpperCase();
                    const constructedFullName = [memberUser?.firstName, memberUser?.lastName].filter(Boolean).join(' ');
                    const rawDisplayName = memberUser?.fullName || 
                                         constructedFullName || 
                                         memberUser?.username || 
                                         memberUser?.email || 
                                         `User ${memberUser?.id || member?.userId || 'Unknown'}`;
                    
                    // Truncate long names to keep them on one line
                    const maxNameLength = 20;
                    const displayName = rawDisplayName.length > maxNameLength 
                                      ? rawDisplayName.substring(0, maxNameLength - 3) + '...'
                                      : rawDisplayName;
                    const profileHrefId = memberUser?.id || member?.userId;
                    const usernameText = memberUser?.username ? `@${memberUser.username}` : '';

                  // Check if this member is the current user
                  const isCurrentUser = (memberUser?.id || member?.userId) === user?.id;
                  
                  return (
                      <ListItem key={member.userId || profileHrefId} divider sx={{ alignItems: 'flex-start' }}>
                      <ListItemAvatar>
                          <Avatar
                            src={
                              memberUser?.avatar
                                ? usersAPI.getProfilePicture(memberUser.avatar)
                                : (memberUser?.avatar || resolveProfilePictureUrl(memberUser?.profilePicture) || 'https://via.placeholder.com/150')
                            }
                            sx={{ width: 32, height: 32, mt: 0.5 }}
                          />
                      </ListItemAvatar>
                      <ListItemText
                        primary={
                            <Box sx={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                              <Box sx={{ display: 'flex', alignItems: 'center', width: '40%', minWidth: 0, pr: 1 }}>
                                <Tooltip title={rawDisplayName.length > maxNameLength ? rawDisplayName : ''} arrow>
                                  <Typography
                                    variant="body1"
                                    sx={{ 
                                      cursor: 'pointer', 
                                      color: '#1976d2',
                                      whiteSpace: 'nowrap',
                                      overflow: 'hidden',
                                      textOverflow: 'ellipsis',
                                      width: '100%'
                                    }}
                                    onClick={() => handleUserClick(profileHrefId)}
                                  >
                                      {displayName}
                                  </Typography>
                                </Tooltip>
                              </Box>
                              <Box sx={{ display: 'flex', alignItems: 'center', width: isOwner && statusUpper === 'APPLIED' ? '30%' : '60%', justifyContent: 'flex-start', gap: 1 }}>
                                <Chip
                                    label={(member.userPlanStatus ? String(member.userPlanStatus).replace(/_/g, ' ') : 'member')}
                                    color={getUserPlanStatusColor(member.userPlanStatus?.toLowerCase())}
                                  size="small"
                                  sx={{ 
                                    fontSize: '0.65rem',
                                    height: '20px'
                                  }}
                                />
                                {isCurrentUser && (
                                  <Chip
                                    label="You"
                                    color="primary"
                                    size="small"
                                    variant="outlined"
                                    sx={{ 
                                      fontSize: '0.7rem',
                                      height: '20px',
                                      fontWeight: 'bold'
                                    }}
                                  />
                                )}
                              </Box>
                              {isOwner && statusUpper === 'APPLIED' && (
                                <Box sx={{ display: 'flex', gap: 0.5, width: '30%', justifyContent: 'flex-end' }}>
                                  <Tooltip title="Accept" arrow>
                                    <Button
                                      variant="contained"
                                      color="success"
                                      size="small"
                                      onClick={() => handleApplicationDecision(member.userId, 'accept')}
                                      sx={{ minWidth: 0, p: 0.5 }}
                                    >
                                      <Check fontSize="small" />
                                    </Button>
                                  </Tooltip>
                                  <Tooltip title="Reject" arrow>
                                    <Button
                                      variant="outlined"
                            color="error"
                                      size="small"
                                      onClick={() => handleApplicationDecision(member.userId, 'reject')}
                                      sx={{ minWidth: 0, p: 0.5 }}
                                    >
                                      <Close fontSize="small" />
                                    </Button>
                                  </Tooltip>
                                </Box>
                              )}
                            </Box>
                          }
                          secondary={usernameText}
                        />
                    </ListItem>
                  );
                })}
              </List>
            </Paper>
            </Box>
          </Box>
        </Paper>
      </Container>

      {/* Invitation FABs for invited user */}
      {plan?.userPlanStatus === 'INVITED' && (
        <>
          <Tooltip title="Accept Invitation" arrow>
            <Fab
              color="primary"
              aria-label="accept-invitation"
              type="submit"
              onClick={() => handleInvitationDecision('accept')}
              sx={{
                position: 'fixed',
                bottom: 140,
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
              <Check />
            </Fab>
          </Tooltip>
          <Tooltip title="Refuse Invitation" arrow>
            <Fab
              color="primary"
              aria-label="refuse-invitation"
              type="submit"
              onClick={() => handleInvitationDecision('reject')}
              sx={{
                position: 'fixed',
                bottom: 70,
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
              <Close />
            </Fab>
          </Tooltip>
        </>
      )}

      {/* Owner close plan FAB when status is NEW */}
      {plan?.userPlanStatus === 'OWNED' && (String(plan.planStatus || plan.status || '').toUpperCase() === 'NEW') && (
        <Tooltip title="Close Plan" arrow>
          <Fab
            color="primary"
            aria-label="close-plan"
            onClick={handleOpenCloseDialog}
            sx={{
              position: 'fixed',
              bottom: 120,
              right: 24,
              backgroundColor: '#f43d65',
              '&:hover': { backgroundColor: '#f297ab' },
              zIndex: 1000
            }}
          >
            Close
          </Fab>
        </Tooltip>
      )}

      {/* Apply/Cancel FAB based on userPlanStatus and hasCurrentPlan */}
      {(() => {
        const statusUpper = (plan?.userPlanStatus || '').toString().toUpperCase();
        const blockedStatuses = [
          'APPLIED',
          'APPLIED_CANCELLED',
          'APPLIED_ACCEPTED',
          'APPLIED_REFUSED',
          'INVITED',
          'INVITED_ACCEPTED',
          'INVITED_REFUSED',
          'OWNED'
        ];
        return plan?.hasCurrentPlan === false && !blockedStatuses.includes(statusUpper) && !isFull;
      })() && (
        <Tooltip title="Apply to this plan" arrow>
          <Fab
            color="primary"
            aria-label="apply-to-plan"
            onClick={handleApply}
            sx={{
              position: 'fixed',
              bottom: 120,
              right: 24,
              backgroundColor: '#f43d65',
              '&:hover': { backgroundColor: '#f297ab' },
              zIndex: 1000
            }}
          >
            Apply
          </Fab>
        </Tooltip>
      )}
      {plan?.userPlanStatus === 'APPLIED' && (
        <Tooltip title="Cancel application" arrow>
          <Fab
            color="primary"
            aria-label="cancel-application"
            onClick={handleCancelApplication}
            sx={{
              position: 'fixed',
              bottom: 120,
              right: 24,
              backgroundColor: '#f43d65',
              '&:hover': { backgroundColor: '#f297ab' },
              zIndex: 1000
            }}
          >
            Cancel
          </Fab>
        </Tooltip>
      )}

      {/* In-progress feature FABs: Chat, Poll, Shared Expense */}
      {(() => {
        const planStatusUpper = String(plan?.planStatus || plan?.status || '').toUpperCase();
        const userStatusUpper = String(plan?.userPlanStatus || '').toUpperCase();
        const allowed = ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'];
        return planStatusUpper === 'IN_PROGRESS' && allowed.includes(userStatusUpper);
      })() && (
        <>
          <Tooltip title="Chat Room" arrow>
            <Fab
              color="primary"
              aria-label="chat-room"
              onClick={() => navigate(`/chat/${planId}`)}
              sx={{
                position: 'fixed',
                bottom: 210,
                right: 24,
                backgroundColor: '#f43d65',
                '&:hover': { backgroundColor: '#f297ab' },
                zIndex: 1000
              }}
            >
              <Chat />
            </Fab>
          </Tooltip>
          <Tooltip title="Poll" arrow>
            <Fab
              color="primary"
              aria-label="poll"
              onClick={() => navigate(`/poll/${planId}`)}
              sx={{
                position: 'fixed',
                bottom: 140,
                right: 24,
                backgroundColor: '#f43d65',
                '&:hover': { backgroundColor: '#f297ab' },
                zIndex: 1000
              }}
            >
              <Poll />
            </Fab>
          </Tooltip>
          <Tooltip title="Shared Expense" arrow>
            <Fab
              color="primary"
              aria-label="shared-expense"
              onClick={() => navigate(`/expense/${planId}`)}
              sx={{
                position: 'fixed',
                bottom: 70,
                right: 24,
                backgroundColor: '#f43d65',
                '&:hover': { backgroundColor: '#f297ab' },
                zIndex: 1000
              }}
            >
              <AttachMoney />
            </Fab>
          </Tooltip>
        </>
      )}

      {/* Close Plan Confirmation Dialog */}
      <Dialog open={closeDialogOpen} onClose={handleCloseCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Close Plan</DialogTitle>
        <DialogContent>
          <Alert 
            severity="warning" 
            sx={{ 
              mb: 2,
              '& .MuiAlert-message': {
                fontSize: '1rem',
                fontWeight: 200,
                color: '#ed6c02'
              },
              '& .MuiAlert-icon': {
                fontSize: '1.5rem'
              },
              backgroundColor: '#fff3e0',
              border: '2px solid #ff9800',
              borderRadius: '8px',
              boxShadow: '0 2px 8px rgba(255, 152, 0, 0.2)'
            }}
          >
            Are you sure you want to close this plan? This action cannot be undone and will notify all members.
          </Alert>
          <TextField
            fullWidth
            label="Reason for closing (required)"
            multiline
            rows={3}
            value={closeReason}
            onChange={(e) => setCloseReason(e.target.value)}
            placeholder="Please provide a reason for closing this plan..."
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseCloseDialog}>Cancel</Button>
          <Button 
            onClick={handleClosePlan} 
            variant="contained" 
            color="error"
            disabled={!closeReason.trim()}
          >
            Close Plan
          </Button>
        </DialogActions>
      </Dialog>

      {/* Invite Dialog */}
      <Dialog open={inviteDialogOpen} onClose={handleCloseInviteDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Invite Member</DialogTitle>
        <DialogContent>
          {inviteError && (
            <Alert 
              severity="error" 
              sx={{ 
                mb: 2,
                '& .MuiAlert-message': {
                  fontSize: '1rem',
                  fontWeight: 200,
                  color: '#d32f2f'
                },
                '& .MuiAlert-icon': {
                  fontSize: '1.5rem'
                },
                backgroundColor: '#ffebee',
                border: '2px solid #f44336',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(244, 67, 54, 0.2)'
              }}
            >
              {inviteError}
            </Alert>
          )}
          {inviteSuccess && (
            <Alert 
              severity="success" 
              sx={{ 
                mb: 2,
                '& .MuiAlert-message': {
                  fontSize: '1rem',
                  fontWeight: 200,
                  color: '#2e7d32'
                },
                '& .MuiAlert-icon': {
                  fontSize: '1.5rem'
                },
                backgroundColor: '#e8f5e8',
                border: '2px solid #4caf50',
                borderRadius: '8px',
                boxShadow: '0 2px 8px rgba(76, 175, 80, 0.2)'
              }}
            >
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
          <Button onClick={handleCloseInviteDialog} disabled={isInviting}>
            Cancel
          </Button>
          <Button 
            onClick={handleSendInvitation} 
            variant="contained"
            disabled={isInviting || !inviteEmail}
          >
            {isInviting ? 'Sending...' : 'Send Invitation'}
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        sx={{ zIndex: 2000 }}
      >
        <Alert 
          onClose={() => setSnackbarOpen(false)} 
          severity={snackbarSeverity} 
          sx={{ 
            width: '100%',
            '& .MuiAlert-message': {
              fontSize: '1rem',
              fontWeight: 200,
              color: snackbarSeverity === 'error' ? '#d32f2f' : snackbarSeverity === 'success' ? '#2e7d32' : 'inherit'
            },
            '& .MuiAlert-icon': {
              fontSize: '1.5rem'
            },
            backgroundColor: snackbarSeverity === 'error' ? '#ffebee' : snackbarSeverity === 'success' ? '#e8f5e8' : 'inherit',
            border: snackbarSeverity === 'error' ? '2px solid #f44336' : snackbarSeverity === 'success' ? '2px solid #4caf50' : 'none',
            borderRadius: '8px',
            boxShadow: snackbarSeverity === 'error' ? '0 2px 8px rgba(244, 67, 54, 0.2)' : snackbarSeverity === 'success' ? '0 2px 8px rgba(76, 175, 80, 0.2)' : 'none'
          }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>

      {/* User Info Dialog */}
      <UserInfoDialog
        open={userInfoDialogOpen}
        onClose={handleCloseUserInfoDialog}
        userId={selectedUserId}
      />
    </PageContainer>
  );
};

export default PlanDetails; 