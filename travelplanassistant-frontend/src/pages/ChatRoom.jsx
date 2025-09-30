import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Container, Typography, Paper, TextField, Button, List, ListItem, ListItemText, Avatar, Divider, CircularProgress, Alert, Snackbar } from '@mui/material';
import { Send } from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';
import PageHeader from '../components/PageHeader';
import PageContainer from '../components/PageContainer';
import { WS_CONFIG, STORAGE_KEYS } from '../config/config';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { chatAPI, usersAPI } from '../services/api';

const ChatRoom = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const messagesEndRef = useRef(null);
  const stompClientRef = useRef(null);

  // Load chat history
  useEffect(() => {
    const loadChatHistory = async () => {
      try {
        setLoading(true);
        const response = await chatAPI.getRecentMessages(planId, 100);
        // Sort messages by timestamp to ensure correct order
        const sortedMessages = (response || []).sort((a, b) => {
          const timeA = new Date(a.timestamp || a.createdAt).getTime();
          const timeB = new Date(b.timestamp || b.createdAt).getTime();
          return timeA - timeB; // Ascending order (oldest first)
        });
        setMessages(sortedMessages);
      } catch (err) {
        console.error('Failed to load chat history:', err);
        setError(err.message || 'Failed to load chat history');
        setSnackbarOpen(true);
      } finally {
        setLoading(false);
      }
    };

    if (planId) {
      loadChatHistory();
    }
  }, [planId]);

  // Initialize WebSocket connection
  useEffect(() => {
    if (!planId) return;

    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    console.log(token);
    if (!token) {
      setError('Authentication required for chat');
      setSnackbarOpen(true);
      return;
    }

    // Create SockJS connection with authentication headers
    const socket = new SockJS(WS_CONFIG.SOCKJS_URL, null, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: { 
        'Authorization': `Bearer ${token}` 
      },
      reconnectDelay: 5000,
      debug: () => {}
    });

    client.onConnect = () => {
      client.subscribe(`/topic/chat/${planId}`, (message) => {
        try {
          const payload = JSON.parse(message.body);
          setMessages((prev) => {
            // Add new message and sort to maintain chronological order
            const updatedMessages = [...prev, payload];
            return updatedMessages.sort((a, b) => {
              const timeA = new Date(a.timestamp || a.createdAt).getTime();
              const timeB = new Date(b.timestamp || b.createdAt).getTime();
              return timeA - timeB; // Ascending order (oldest first)
            });
          });
        } catch (err) {
          console.error('Error parsing WebSocket message:', err);
        }
      });
    };

    client.onStompError = (frame) => {
      console.error('WebSocket error:', frame);
      setError('Connection error. Please refresh the page.');
      setSnackbarOpen(true);
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      try {
        client.deactivate();
      } catch (err) {
        console.error('Error deactivating WebSocket:', err);
      }
    };
  }, [planId]);

  useEffect(() => {
    // Scroll to bottom when new messages arrive
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = () => {
    if (!newMessage.trim() || !user) return;
    const client = stompClientRef.current;
    if (!client || !client.connected) {
      setError('Not connected to chat server. Please refresh the page.');
      setSnackbarOpen(true);
      return;
    }
    
    const request = { 
      content: newMessage.trim(),
      messageType: 'TEXT'
    };
    
    try {
      client.publish({ 
        destination: `/app/chat/${planId}`, 
        body: JSON.stringify(request)
      });
      setNewMessage('');
    } catch (err) {
      console.error('Error sending message:', err);
      setError('Failed to send message. Please try again.');
      setSnackbarOpen(true);
    }
  };

  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
    setError(null);
  };

  const handleKeyDown = (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      handleSendMessage();
    }
  };

  const formatTime = (timestamp) => {
    return new Date(timestamp).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const resolveUserAvatar = (avatarPath) => {
    if (!avatarPath) return null;
    if (avatarPath.startsWith('http')) return avatarPath;
    return usersAPI.getProfilePicture(avatarPath);
  };

  return (
    <PageContainer>
      <PageHeader title="Chat Room" onBack={() => navigate(`/plan/${planId}`)}>
        <Typography variant="subtitle1" sx={{ mt: 3 }}>
          Chat with your travel partners
        </Typography>
      </PageHeader>

      <Container maxWidth="md" sx={{ position: 'relative', zIndex: 1 }}>
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
          {/* Messages */}
          <Box sx={{ maxHeight: '60vh', overflow: 'auto', px: 1, pb: 2 }}>
            {loading ? (
              <Box sx={{ textAlign: 'center', py: 8 }}>
                <CircularProgress />
                <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                  Loading chat history...
                </Typography>
              </Box>
            ) : messages.length === 0 ? (
              <Box sx={{ textAlign: 'center', py: 8 }}>
                <Typography variant="h6" color="text.secondary" gutterBottom>
                  No messages yet
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Start the conversation!
                </Typography>
              </Box>
            ) : (
              <List sx={{ width: '100%' }}>
                {messages.map((message, index) => (
                  <React.Fragment key={message.id || index}>
                    <ListItem
                      sx={{
                        flexDirection: 'column',
                        alignItems: message.userId === user?.id ? 'flex-end' : 'flex-start',
                        px: 0
                      }}
                    >
                      <Box
                        sx={{
                          display: 'flex',
                          alignItems: 'flex-start',
                          gap: 1,
                          maxWidth: '70%',
                          flexDirection: message.userId === user?.id ? 'row-reverse' : 'row'
                        }}
                      >
                        <Avatar
                          src={resolveUserAvatar(message.userAvatar)}
                          sx={{ 
                            width: 36, 
                            height: 36, 
                            flexShrink: 0,
                            border: 2,
                            borderColor: message.userId === user?.id ? 'white' : 'grey.300',
                            boxShadow: 1
                          }}
                        >
                          {message.userName ? message.userName.charAt(0).toUpperCase() : '?'}
                        </Avatar>
                        <Paper
                          elevation={1}
                          sx={{
                            p: 2,
                            background: message.userId === user?.id 
                              ? 'linear-gradient(135deg, #f43d65 0%, #f297ab 100%)' 
                              : '#f5f5f5',
                            color: message.userId === user?.id ? 'white' : 'text.primary',
                            borderRadius: 2,
                            maxWidth: '100%'
                          }}
                        >
                          <Typography variant="body1" sx={{ wordBreak: 'break-word' }}>
                            {message.content || message.text}
                          </Typography>
                          <Typography
                            variant="caption"
                            sx={{
                              display: 'block',
                              mt: 1,
                              opacity: 0.7,
                              textAlign: message.userId === user?.id ? 'right' : 'left'
                            }}
                          >
                            {formatTime(message.timestamp || message.createdAt)}
                          </Typography>
                        </Paper>
                      </Box>
                      {message.userId !== user?.id && (
                        <Typography
                          variant="caption"
                          sx={{
                            mt: 0.5,
                            color: 'text.secondary',
                            alignSelf: 'flex-start'
                          }}
                        >
                          {message.userName}
                        </Typography>
                      )}
                    </ListItem>
                    {index < messages.length - 1 && (
                      <Divider sx={{ my: 1 }} />
                    )}
                  </React.Fragment>
                ))}
                <div ref={messagesEndRef} />
              </List>
            )}
          </Box>

          {/* Message Input */}
          <Box sx={{ pt: 2, borderTop: 1, borderColor: 'divider' }}>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <TextField
                fullWidth
                placeholder="Type a message..."
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                onKeyDown={handleKeyDown}
                multiline
                maxRows={4}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    borderRadius: 3,
                  }
                }}
              />
              <Button
                variant="contained"
                onClick={handleSendMessage}
                disabled={!newMessage.trim()}
                sx={{
                  minWidth: 56,
                  height: 56,
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, #f43d65 0%, #f297ab 100%)',
                  '&:hover': {
                    background: 'linear-gradient(135deg, #e63946 0%, #f1828d 100%)',
                  }
                }}
              >
                <Send />
              </Button>
            </Box>
          </Box>
        </Paper>
      </Container>

      {/* Error Snackbar */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert onClose={handleSnackbarClose} severity="error" sx={{ width: '100%' }}>
          {error}
        </Alert>
      </Snackbar>
    </PageContainer>
  );
};

export default ChatRoom; 