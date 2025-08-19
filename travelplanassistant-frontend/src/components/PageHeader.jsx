import React from 'react';
import {
  Box,
  Container,
  Typography,
  Button,
  IconButton
} from '@mui/material';
import {
  ArrowBack,
  Logout
} from '@mui/icons-material';

const PageHeader = ({
  title,
  onBack,
  onLogout,
  children
}) => {
  return (
    <>
      {/* Header */}
      <Box
        sx={{
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100%',
          zIndex: 1200,
          background: 'linear-gradient(135deg, #f43d65 0%, #f297ab 100%)',
          color: 'white',
          height: 150,
          px: 2,
          boxShadow: 3,
          display: 'flex',
          flexDirection: 'column', // stack vertically
          justifyContent: 'flex-start', // align to top
        }}
      >
        <Container maxWidth="lg" sx={{ width: '100%', height: '100%' }}>
          <Box sx={{ 
            display: 'flex', 
            alignItems: 'flex-start', // align items to the top
            justifyContent: onLogout ? 'space-between' : 'flex-start',
            mb: children ? 2 : 0,
            minHeight: 48,
            pt: 2 // add some top padding if needed
          }}>
            {onBack && (
              <IconButton
                onClick={onBack}
                sx={{ color: 'white', mr: 2, mt: 0 }}
              >
                <ArrowBack />
              </IconButton>
            )}
            <Typography variant="h5" component="h1" sx={{ fontWeight: 'bold', mt: 0 }}>
              {title}
            </Typography>
            {onLogout && (
              <Button
                variant="outlined"
                startIcon={<Logout />}
                onClick={onLogout}
                sx={{
                  color: 'white',
                  borderColor: 'rgba(255, 255, 255, 1)',
                  '&:hover': {
                    borderColor: 'white',
                    backgroundColor: 'rgba(255, 255, 255, 0.1)',
                  },
                  mt: 0
                }}
              >
                Logout
              </Button>
            )}
          </Box>
          {/* Custom children for page-specific elements */}
          {children}
        </Container>
      </Box>
      
      {/* Spacer to prevent content from being hidden behind the fixed header */}
      <Box sx={{ height: children ? 120 : 80 }} />
    </>
  );
};

export default PageHeader; 