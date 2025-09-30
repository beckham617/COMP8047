import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Paper,
  Button,
  List,
  ListItem,
  ListItemText,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormControl,
  Divider,
  Fab,
  Tooltip,
  Snackbar,
  Alert,
  IconButton,
  Avatar,
  LinearProgress,
  CircularProgress
} from '@mui/material';
import {
  Add,
  Send,
  HowToVote
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';
import { pollsAPI, usersAPI } from '../services/api';

const Poll = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [polls, setPolls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');
  const [selectedOptions, setSelectedOptions] = useState({});

  useEffect(() => {
    loadPolls();
  }, [planId]);

  // Polling for poll updates every 8 seconds to reflect new votes and polls
  useEffect(() => {
    if (!planId) return;

    const pollUpdates = async () => {
      try {
        const response = await pollsAPI.getPolls(planId);
        const newPolls = response || [];
        
        setPolls(prevPolls => {
          // Only update if there are actual changes to avoid unnecessary re-renders
          if (JSON.stringify(prevPolls) !== JSON.stringify(newPolls)) {
            const prevPollIds = new Set(prevPolls.map(p => p.id));
            const newPollIds = new Set(newPolls.map(p => p.id));
            
            // Check for new polls
            const addedPolls = newPolls.filter(poll => !prevPollIds.has(poll.id));
            
            // Check for updated polls (vote count changes)
            const updatedPolls = newPolls.filter(poll => {
              const prevPoll = prevPolls.find(p => p.id === poll.id);
              return prevPoll && JSON.stringify(prevPoll.voteCounts) !== JSON.stringify(poll.voteCounts);
            });
            
            if (addedPolls.length > 0) {
              console.log('Poll: New polls detected', { 
                added: addedPolls.length, 
                pollTitles: addedPolls.map(p => p.question) 
              });
              
              // Show notification for new polls
              if (addedPolls.length === 1) {
                setSnackbarMessage(`New poll: "${addedPolls[0].question}"`);
              } else {
                setSnackbarMessage(`${addedPolls.length} new polls available!`);
              }
              setSnackbarSeverity('success');
              setSnackbarOpen(true);
            }
            
            if (updatedPolls.length > 0) {
              console.log('Poll: Vote counts updated', { 
                updated: updatedPolls.length, 
                pollTitles: updatedPolls.map(p => p.question) 
              });
            }
            
            return newPolls;
          }
          return prevPolls;
        });
        
        // Update selected options for polls where user has already voted
        if (newPolls && newPolls.length > 0) {
          setSelectedOptions(prev => {
            const updated = { ...prev };
            newPolls.forEach(poll => {
              if (poll.hasUserVoted) {
                delete updated[poll.id];
              }
            });
            return updated;
          });
        }
      } catch (err) {
        console.error('Failed to poll poll updates:', err);
      }
    };

    // Initial poll after a short delay
    const initialTimeout = setTimeout(pollUpdates, 3000);

    // Set up polling interval
    const pollInterval = setInterval(pollUpdates, 8000); // Poll every 8 seconds

    return () => {
      clearTimeout(initialTimeout);
      clearInterval(pollInterval);
    };
  }, [planId]);

  const loadPolls = async () => {
    try {
      setLoading(true);
      const response = await pollsAPI.getPolls(planId);
      setPolls(response || []);
      
      // Clear selections for polls where user has already voted
      if (response && response.length > 0) {
        setSelectedOptions(prev => {
          const updated = { ...prev };
          response.forEach(poll => {
            if (poll.hasUserVoted) {
              delete updated[poll.id];
            }
          });
          return updated;
        });
      }
    } catch (err) {
      console.error('Failed to load polls:', err);
      setSnackbarMessage(err.message || 'Failed to load polls');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    } finally {
      setLoading(false);
    }
  };

  const handleVote = async (pollId) => {
    if (!user) {
      setSnackbarMessage('You must be logged in to vote');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
      return;
    }

    const selectedOptionId = selectedOptions[pollId];
    if (selectedOptionId === undefined || selectedOptionId === '') {
      setSnackbarMessage('Please select an option before voting');
      setSnackbarSeverity('warning');
      setSnackbarOpen(true);
      return;
    }

    try {
      // Use the option ID directly from the selection
      await pollsAPI.vote(pollId, selectedOptionId);
      
      // Reload polls to get updated vote counts
      await loadPolls();
      
      // Don't clear selection immediately - let the updated poll data handle the display
      // The RadioGroup will show the vote based on poll.userVote.optionId from the backend
      
      setSnackbarMessage('Vote submitted successfully!');
      setSnackbarSeverity('success');
      setSnackbarOpen(true);
    } catch (err) {
      console.error('Failed to submit vote:', err);
      setSnackbarMessage(err.message || 'Failed to submit vote');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    }
  };

  const handleOptionChange = (pollId, optionId) => {
    setSelectedOptions(prev => ({
      ...prev,
      [pollId]: optionId
    }));
  };

  const resolveCreatorAvatar = (avatarPath) => {
    if (!avatarPath) return null;
    if (avatarPath.startsWith('http')) return avatarPath;
    return usersAPI.getProfilePicture(avatarPath);
  };


  const formatDate = (timestamp) => {
    return new Date(timestamp).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };

  return (
    <PageContainer>
      <PageHeader title="Polls" onBack={() => navigate(`/plan/${planId}`)}>
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          Vote on travel decisions
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
            <Box sx={{ textAlign: 'center', py: 8 }}>
              <CircularProgress />
              <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                Loading polls...
              </Typography>
            </Box>
          ) : polls.length === 0 ? (
            <Box sx={{ textAlign: 'center', py: 8 }}>
              <Typography variant="h6" color="text.secondary" gutterBottom>
                No polls yet
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 11 }}>
                Create the first poll for this plan!
              </Typography>
            </Box>
          ) : (
            <List>
              {polls.map((poll) => {
                const hasVoted = poll.hasUserVoted;
                const totalVotes = Object.values(poll.voteCounts || {}).reduce((sum, count) => sum + count, 0);

                return (
                  <Paper key={poll.id} elevation={2} sx={{ mb: 3, p: 3, borderRadius: 2 }}>
                    {/* Poll Header */}
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <Avatar
                        src={resolveCreatorAvatar(poll.creatorAvatar)}
                        sx={{ width: 32, height: 32, mr: 2 }}
                      >
                        {poll.creatorName ? poll.creatorName.charAt(0).toUpperCase() : '?'}
                      </Avatar>
                      <Box sx={{ flexGrow: 1 }}>
                        <Typography variant="body2" color="text.secondary">
                          {poll.creatorName}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {formatDate(poll.createdAt)}
                        </Typography>
                      </Box>
                      {!hasVoted && (
                        <IconButton
                          color="primary"
                          onClick={() => handleVote(poll.id)}
                          disabled={selectedOptions[poll.id] === undefined || selectedOptions[poll.id] === ''}
                          sx={{
                            background: 'linear-gradient(135deg, #f43d65 0%, #f297ab 100%)',
                            color: 'white',
                            '&:hover': {
                              background: 'linear-gradient(135deg, #e63946 0%, #f1828d 100%)',
                            },
                            '&:disabled': {
                              background: '#e0e0e0',
                              color: '#9e9e9e',
                            }
                          }}
                        >
                          <Send />
                        </IconButton>
                      )}
                    </Box>

                    {/* Poll Question */}
                    <Typography variant="h6" gutterBottom sx={{ mb: 3 }}>
                      {poll.question}
                    </Typography>

                    {/* Poll Options */}
                    <FormControl component="fieldset" sx={{ width: '100%' }}>
                      <RadioGroup
                        value={selectedOptions[poll.id] !== undefined ? selectedOptions[poll.id] : (poll.userVote ? String(poll.userVote.optionId) : '')}
                        onChange={(e) => handleOptionChange(poll.id, e.target.value)}
                      >
                        {Object.entries(poll.options || {}).map(([optionId, optionText]) => {
                          const voteCount = poll.voteCounts?.[optionText] || 0;
                          const percentage = totalVotes > 0 ? Math.round((voteCount / totalVotes) * 100) : 0;
                          const isUserVote = poll.userVote?.optionId === parseInt(optionId);

                          return (
                            <Box key={`${optionId}-${optionText}`} sx={{ mb: 1 }}>
                              <FormControlLabel
                                value={optionId}
                                control={<Radio />}
                                label={
                                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                                    <Typography variant="body1" sx={{ flex: 1, pr: 3 }}>
                                      {optionText}
                                    </Typography>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, flexShrink: 0, textAlign: 'right' }}>
                                      <Typography variant="body2" color="text.secondary" sx={{ textAlign: 'right' }}>
                                        {voteCount} votes
                                      </Typography>
                                      <Typography variant="body2" color="primary" sx={{ textAlign: 'right' }}>
                                        ({percentage}%)
                                      </Typography>
                                    </Box>
                                  </Box>
                                }
                                disabled={hasVoted}
                                sx={{
                                  width: '100%',
                                  margin: 0,
                                  padding: 1,
                                  borderRadius: 1,
                                  backgroundColor: isUserVote ? 'rgba(244, 61, 101, 0.1)' : 'transparent',
                                  display: 'flex',
                                  alignItems: 'center',
                                  '& .MuiFormControlLabel-label': {
                                    flex: 1,
                                    width: '100%'
                                  },
                                  '&:hover': {
                                    backgroundColor: hasVoted 
                                      ? (isUserVote ? 'rgba(244, 61, 101, 0.1)' : 'transparent')
                                      : 'rgba(0, 0, 0, 0.04)',
                                  }
                                }}
                              />
                              {/* Vote Progress Bar */}
                              <LinearProgress
                                variant="determinate"
                                value={percentage}
                                sx={{
                                  height: 4,
                                  borderRadius: 2,
                                  backgroundColor: 'rgba(0, 0, 0, 0.1)',
                                  '& .MuiLinearProgress-bar': {
                                    backgroundColor: isUserVote ? '#f43d65' : '#1976d2',
                                  }
                                }}
                              />
                            </Box>
                          );
                        })}
                      </RadioGroup>
                    </FormControl>

                    {/* Poll Footer */}
                    <Box sx={{ mt: 2, pt: 2, borderTop: 1, borderColor: 'divider' }}>
                      <Typography variant="body2" color="text.secondary">
                        Total votes: {totalVotes}
                        {hasVoted && (
                          <Typography component="span" sx={{ ml: 2, color: 'success.main' }}>
                            • You have voted
                          </Typography>
                        )}
                      </Typography>
                    </Box>
                  </Paper>
                );
              })}
            </List>
        )}
        </Paper>
      </Container>

      {/* Floating Action Button */}
      <Tooltip title="Create Poll" arrow>
        <Fab
          color="primary"
          aria-label="create poll"
          onClick={() => navigate(`/create-poll/${planId}`)}
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

export default Poll; 