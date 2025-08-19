import React from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Box, 
  Button, 
  Typography, 
  Container,
  Paper
} from '@mui/material';
import backgroundImage from '../assets/background.avif';

const Welcome = () => {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        minHeight: '100vh',
        backgroundImage: `url(${backgroundImage})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(255, 255, 255, 0.3)',
        }
      }}
    >
      <Paper
        elevation={0}
        sx={{
          p: 6,
          width: { xs: '100vw', sm: '90vw', md: '70vw' },
          maxWidth: 900,
          minHeight: { xs: '98vh', sm: '85vh' },
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between',
          alignItems: 'center',
          textAlign: 'center',
          backgroundColor: 'transparent',
          borderRadius: 3,
          position: 'relative',
          zIndex: 1,
        }}
      >
        <Box sx={{ width: '100%', mt: 30 }}>
          <Typography
            variant="h3"
            component="h1"
            gutterBottom
            sx={{
              fontFamily: 'Lemon',
              fontWeight: 'bold',
              color: '#f43d65',
              mb: 4,
              fontSize: { xs: '2.2rem', sm: '3rem', md: '3.5rem' }
            }}
          >
            Let's GO
          </Typography>
          <Typography
            variant="h6"
            color="text.secondary"
            sx={{ mb: 4, color: '#f43d65', fontSize: { xs: '1.1rem', sm: '1.3rem', md: '1.5rem' } }}
          >
            Discover, create, and manage your travel plans with friends
          </Typography>
        </Box>
        <Box sx={{ width: '100%', display: 'flex', flexDirection: 'row', gap: 2, justifyContent: 'space-between', mb: 2, maxWidth: 400, mx: 'auto' }}>
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate('/register')}
            sx={{
              width: { xs: '100%', sm: 220, md: 260 },
              fontSize: { xs: '1.1rem', sm: '1.2rem', md: '1.3rem' },
              py: 1.5,
              fontWeight: 'bold',
              backgroundColor: '#f43d65',
              '&:hover': {
                backgroundColor: '#f297ab',
              }
            }}
          >
            Register
          </Button>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate('/login')}
            sx={{
              width: { xs: '100%', sm: 220, md: 260 },
              fontSize: { xs: '1.1rem', sm: '1.2rem', md: '1.3rem' },
              py: 1.5,
              fontWeight: 'bold',
              borderColor: '#f43d65',
              color: '#f43d65',
              '&:hover': {
                borderColor: '#f297ab',
                backgroundColor: '#f297ab',
              }
            }}
          >
            Log In
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default Welcome; 