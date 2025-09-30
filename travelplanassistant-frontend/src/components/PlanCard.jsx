import React from 'react';
import { Card, CardContent, CardMedia, Typography, Box, Avatar, Chip } from '@mui/material';
import { usersAPI } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

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
      return 'success';
    case 'applied':
      return 'warning';
    case 'applied_accepted':
      return 'success';
    case 'applied_refused':
      return 'error';
    case 'applied_cancelled':
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

const PlanCard = ({ plan, onClick }) => {
  const { user } = useAuth();
  
  const planStatusRaw = (plan.planStatus || plan.status || 'new').toString().toLowerCase();
  const planStatusLabel = (plan.planStatus || plan.status || 'NEW').toString().replace(/_/g, ' ');
  const ownerAvatar = plan.ownerAvatar ? usersAPI.getProfilePicture(plan.ownerAvatar) : (plan.ownerAvatar || 'https://via.placeholder.com/150');
  const coverImage = (Array.isArray(plan.images) && plan.images.length > 0)
    ? usersAPI.getProfilePicture(plan.images[0])
    : (plan.coverImage || 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?ixlib=rb-4.0.3&auto=format&fit=crop&w=400&h=200&q=80');

  return (
    <Card
      sx={{
        height: '100%',
        width: '100%',
        minHeight: '400px',
        maxWidth: '100%',
        minWidth: '100%',
        display: 'flex',
        flexDirection: 'column',
        cursor: 'pointer',
        transition: 'transform 0.2s, box-shadow 0.2s',
        overflow: 'hidden',
        flexShrink: 0,
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: 4,
        }
      }}
      onClick={onClick}
    >
      <Box
        sx={{
          width: '100%',
          height: 200,
          minWidth: '1  00%',
          position: 'relative',
          overflow: 'hidden',
          backgroundColor: '#f5f5f5',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          flexShrink: 0
        }}
      >
        <CardMedia
          component="img"
          image={coverImage}
          alt={plan.title}
          sx={{ 
            width: '100%', 
            height: '100%',
            minWidth: '100%',
            minHeight: '100%',
            maxWidth: '100%',
            maxHeight: '100%',
            objectFit: 'cover', 
            objectPosition: 'center',
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            display: 'block'
          }}
        />
      </Box>
      <CardContent sx={{ 
        flexGrow: 1, 
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
        height: '100%'
      }}>
        <Typography 
          variant="h6" 
          component="h2" 
          gutterBottom
          sx={{
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            minHeight: '3.5rem'
          }}
        >
          {plan.title}
        </Typography>

        <Typography 
          variant="body2" 
          color="text.secondary" 
          sx={{ 
            mb: 2,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 3,
            WebkitBoxOrient: 'vertical',
            flexGrow: 1
          }}
        >
          {plan.description}
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          <Avatar src={ownerAvatar} sx={{ width: 32, height: 32, mr: 1 }} />
          <Typography variant="body2" color="text.secondary">
            {plan.ownerName}
          </Typography>
        </Box>

        <Box sx={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          marginTop: 'auto',
          pt: 1
        }}>
          <Box sx={{ display: 'flex', gap: 1, alignItems: 'center', flexWrap: 'wrap' }}>
            <Chip
              label={planStatusLabel}
              color={getStatusColor(planStatusRaw)}
              size="small"
            />
            {(() => {
              // Determine user plan status - use provided status or infer from plan data
              let userPlanStatus = plan.userPlanStatus;
              
              // If userPlanStatus is not provided, try to infer it
              if (!userPlanStatus && user && plan) {
                // Check if current user is the owner
                if (plan.ownerId === user.id || plan.owner?.id === user.id) {
                  userPlanStatus = 'OWNED';
                } else if (plan.members && Array.isArray(plan.members)) {
                  // Check if current user is in the members list
                  const userMember = plan.members.find(member => 
                    member.userId === user.id || member.user?.id === user.id
                  );
                  if (userMember) {
                    userPlanStatus = userMember.userPlanStatus || 'MEMBER';
                  }
                }
              }
              
              return userPlanStatus ? (
                <Chip
                  label={userPlanStatus.toString().replace(/_/g, ' ')}
                  color={getUserPlanStatusColor(userPlanStatus.toString().toLowerCase())}
                  size="small"
                />
              ) : null;
            })()}
          </Box>
          <Typography 
            variant="body2" 
            color={(() => {
              const members = Array.isArray(plan.members) ? plan.members : [];
              const acceptedMembers = members.filter(member => 
                ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'].includes(
                  (member.userPlanStatus || '').toString().toUpperCase()
                )
              );
              const currentCount = acceptedMembers.length;
              const maxMembers = plan.maxMembers || members.length;
              return currentCount >= maxMembers ? 'error' : 'text.secondary';
            })()}
            sx={{ 
              fontWeight: (() => {
                const members = Array.isArray(plan.members) ? plan.members : [];
                const acceptedMembers = members.filter(member => 
                  ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'].includes(
                    (member.userPlanStatus || '').toString().toUpperCase()
                  )
                );
                const currentCount = acceptedMembers.length;
                const maxMembers = plan.maxMembers || members.length;
                return currentCount >= maxMembers ? 'bold' : 'normal';
              })(),
              whiteSpace: 'nowrap'
            }}
          >
            {(() => {
              const members = Array.isArray(plan.members) ? plan.members : [];
              const acceptedMembers = members.filter(member => 
                ['OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED'].includes(
                  (member.userPlanStatus || '').toString().toUpperCase()
                )
              );
              const currentCount = acceptedMembers.length;
              const maxMembers = plan.maxMembers || members.length;
              return currentCount >= maxMembers ? `FULL (${currentCount}/${maxMembers})` : `${currentCount}/${maxMembers}`;
            })()}
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
};

export default PlanCard;


