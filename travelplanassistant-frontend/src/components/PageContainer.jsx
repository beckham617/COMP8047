import React from 'react';
import { Box } from '@mui/material';
import backgroundImage from '../assets/background.avif';

const PageContainer = ({ children }) => {
  return (
    <Box
      sx={{
        position: 'relative',
        minHeight: '100vh',
        width: '100vw',
        backgroundImage: `url(${backgroundImage})`,
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        backgroundSize: 'cover',
        backgroundAttachment: 'fixed', // keep background still
        overflow: 'auto',
        py: 4,
        '&::before': {
          content: '""',
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100vw',
          height: '100vh',
          backgroundColor: 'rgba(255, 255, 255, 0.75)',
          zIndex: 0,
          pointerEvents: 'none',
        },
      }}
    >
      {/* Content should be above the overlay */}
      <Box sx={{ position: 'relative', zIndex: 1 }}>
        {children}
      </Box>
    </Box>
  );
};

export default PageContainer; 