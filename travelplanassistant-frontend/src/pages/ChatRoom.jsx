import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Typography,
  Paper,
  TextField,
  Button,
  IconButton,
  List,
  ListItem,
  ListItemText,
  Avatar,
  Divider
} from '@mui/material';
import {
  ArrowBack,
  Send
} from '@mui/icons-material';
import { useAuth } from '../contexts/AuthContext';

const ChatRoom = () => {
  const { planId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const messagesEndRef = useRef(null);

  useEffect(() => {
    // Load chat messages from localStorage
    const chatKey = `chat_${planId}`;
    const storedMessages = JSON.parse(localStorage.getItem(chatKey) || '[]');
    setMessages(storedMessages);
  }, [planId]);

  useEffect(() => {
    // Scroll to bottom when new messages arrive
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = () => {
    if (!newMessage.trim() || !user) return;

    const message = {
      id: Date.now().toString(),
      text: newMessage.trim(),
      userId: user.id,
      userName: `${user.firstName} ${user.lastName}`,
      userAvatar: user.profilePicture,
      timestamp: new Date().toISOString()
    };

    const updatedMessages = [...messages, message];
    setMessages(updatedMessages);

    // Store in localStorage
    const chatKey = `chat_${planId}`;
    localStorage.setItem(chatKey, JSON.stringify(updatedMessages));

    setNewMessage('');
  };

  const handleKeyPress = (event) => {
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

  return (
    <Box sx={{ height: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <Box
        sx={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          color: 'white',
          py: 2,
          px: 2
        }}
      >
        <Container maxWidth="lg">
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <IconButton
              onClick={() => navigate(-1)}
              sx={{ color: 'white', mr: 2 }}
            >
              <ArrowBack />
            </IconButton>
            <Typography variant="h6" component="h1" sx={{ fontWeight: 'bold' }}>
              Chat Room
            </Typography>
          </Box>
        </Container>
      </Box>

      {/* Messages */}
      <Box sx={{ flex: 1, overflow: 'auto', p: 2 }}>
        <Container maxWidth="md">
          {messages.length === 0 ? (
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
                <React.Fragment key={message.id}>
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
                        src={message.userAvatar}
                        sx={{ width: 32, height: 32, flexShrink: 0 }}
                      />
                      <Paper
                        elevation={1}
                        sx={{
                          p: 2,
                          backgroundColor: message.userId === user?.id ? '#1976d2' : '#f5f5f5',
                          color: message.userId === user?.id ? 'white' : 'text.primary',
                          borderRadius: 2,
                          maxWidth: '100%'
                        }}
                      >
                        <Typography variant="body1" sx={{ wordBreak: 'break-word' }}>
                          {message.text}
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
                          {formatTime(message.timestamp)}
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
        </Container>
      </Box>

      {/* Message Input */}
      <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
        <Container maxWidth="md">
          <Box sx={{ display: 'flex', gap: 1 }}>
            <TextField
              fullWidth
              placeholder="Type a message..."
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              onKeyPress={handleKeyPress}
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
                backgroundColor: '#1976d2',
                '&:hover': {
                  backgroundColor: '#1565c0',
                }
              }}
            >
              <Send />
            </Button>
          </Box>
        </Container>
      </Box>
    </Box>
  );
};

export default ChatRoom; 