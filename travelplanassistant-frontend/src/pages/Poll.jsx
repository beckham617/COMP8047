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
  Radio,
  RadioGroup,
  FormControlLabel,
  FormControl,
  Divider,
  Fab
} from '@mui/material';
import {
  ArrowBack,
  Add
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const Poll = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [polls, setPolls] = useState([]);

  useEffect(() => {
    // Load polls from localStorage
    const pollsKey = `polls_${planId}`;
    const storedPolls = JSON.parse(localStorage.getItem(pollsKey) || '[]');
    setPolls(storedPolls);
  }, [planId]);

  const handleVote = (pollId, optionIndex) => {
    if (!user) return;

    const updatedPolls = polls.map(poll => {
      if (poll.id === pollId) {
        // Check if user already voted
        const userVote = poll.votes.find(vote => vote.userId === user.id);
        if (userVote) return poll; // User already voted

        // Add vote
        const newVote = {
          userId: user.id,
          optionIndex: optionIndex,
          timestamp: new Date().toISOString()
        };

        return {
          ...poll,
          votes: [...poll.votes, newVote]
        };
      }
      return poll;
    });

    setPolls(updatedPolls);
    
    // Store in localStorage
    const pollsKey = `polls_${planId}`;
    localStorage.setItem(pollsKey, JSON.stringify(updatedPolls));
  };

  const getUserVote = (poll) => {
    if (!user) return null;
    return poll.votes.find(vote => vote.userId === user.id);
  };

  const getOptionVoteCount = (poll, optionIndex) => {
    return poll.votes.filter(vote => vote.optionIndex === optionIndex).length;
  };

  const getTotalVotes = (poll) => {
    return poll.votes.length;
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
              Polls
            </Typography>
          </Box>
        </Container>
      </Box>

      {/* Polls List */}
      <Container maxWidth="md" sx={{ py: 3 }}>
        {polls.length === 0 ? (
          <Box sx={{ textAlign: 'center', py: 8 }}>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              No polls yet
            </Typography>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Create the first poll for this plan!
            </Typography>
            <Button
              variant="contained"
              startIcon={<Add />}
              onClick={() => navigate(`/create-poll/${planId}`)}
              sx={{
                backgroundColor: '#1976d2',
                '&:hover': {
                  backgroundColor: '#1565c0',
                }
              }}
            >
              Create Poll
            </Button>
          </Box>
        ) : (
          <List>
            {polls.map((poll, pollIndex) => {
              const userVote = getUserVote(poll);
              const totalVotes = getTotalVotes(poll);

              return (
                <Paper key={poll.id} elevation={2} sx={{ mb: 3, p: 3, borderRadius: 2 }}>
                  <Typography variant="h6" gutterBottom>
                    {poll.question}
                  </Typography>
                  
                  <Typography variant="caption" color="text.secondary" sx={{ mb: 2, display: 'block' }}>
                    Created by {poll.createdBy} • {formatDate(poll.createdAt)}
                  </Typography>

                  <FormControl component="fieldset" sx={{ width: '100%' }}>
                    <RadioGroup
                      value={userVote ? userVote.optionIndex : ''}
                      onChange={(e) => handleVote(poll.id, parseInt(e.target.value))}
                    >
                      {poll.options.map((option, optionIndex) => {
                        const voteCount = getOptionVoteCount(poll, optionIndex);
                        const percentage = totalVotes > 0 ? Math.round((voteCount / totalVotes) * 100) : 0;
                        const isVoted = userVote && userVote.optionIndex === optionIndex;

                        return (
                          <FormControlLabel
                            key={optionIndex}
                            value={optionIndex}
                            control={<Radio />}
                            label={
                              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                                <Typography variant="body1">
                                  {option}
                                </Typography>
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                  <Typography variant="body2" color="text.secondary">
                                    {voteCount} votes
                                  </Typography>
                                  <Typography variant="body2" color="primary">
                                    ({percentage}%)
                                  </Typography>
                                </Box>
                              </Box>
                            }
                            disabled={userVote !== null}
                            sx={{
                              width: '100%',
                              margin: 0,
                              padding: 1,
                              borderRadius: 1,
                              backgroundColor: isVoted ? 'rgba(25, 118, 210, 0.1)' : 'transparent',
                              '&:hover': {
                                backgroundColor: userVote ? 'rgba(25, 118, 210, 0.1)' : 'rgba(0, 0, 0, 0.04)',
                              }
                            }}
                          />
                        );
                      })}
                    </RadioGroup>
                  </FormControl>

                  <Box sx={{ mt: 2, pt: 2, borderTop: 1, borderColor: 'divider' }}>
                    <Typography variant="body2" color="text.secondary">
                      Total votes: {totalVotes}
                    </Typography>
                  </Box>
                </Paper>
              );
            })}
          </List>
        )}
      </Container>

      {/* Floating Action Button */}
      <Fab
        color="primary"
        aria-label="create poll"
        onClick={() => navigate(`/create-poll/${planId}`)}
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

export default Poll; 