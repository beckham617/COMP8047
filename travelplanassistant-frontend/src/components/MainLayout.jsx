import React from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Box, Paper, BottomNavigation, BottomNavigationAction } from '@mui/material';
import { Search, Home, Person } from '@mui/icons-material';

const MainLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const getCurrentValue = () => {
    if (location.pathname.includes('/discovery')) return 0;
    if (
      location.pathname.includes('/my-plans') ||
      location.pathname.includes('/create-plan') ||
      location.pathname.includes('/chat/') ||
      location.pathname.includes('/poll/') ||
      location.pathname.includes('/create-poll/') ||
      location.pathname.includes('/expense/') ||
      location.pathname.includes('/create-expense/') ||
      (location.pathname.startsWith('/plan/') && !(location.state && location.state.from === 'discovery'))
    ) return 1;
    if (
      location.pathname === '/profile' ||
      location.pathname === '/edit-profile'
    ) return 2;
    return 0;
  };

  const handleNavigation = (event, newValue) => {
    switch (newValue) {
      case 0:
        navigate('/discovery');
        break;
      case 1:
        navigate('/my-plans');
        break;
      case 2:
        navigate('/profile');
        break;
      default:
        break;
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      <Box sx={{ flex: 1, overflow: 'auto' }}>
        <Outlet />
      </Box>
      <Paper 
        sx={{ 
          position: 'fixed', 
          bottom: 0, 
          left: 0, 
          right: 0, 
          zIndex: 1000 
        }} 
        elevation={3}
      >
        <BottomNavigation
          value={getCurrentValue()}
          onChange={handleNavigation}
          showLabels
        >
          <BottomNavigationAction 
            label="Discovery" 
            icon={<Search />} 
          />
          <BottomNavigationAction 
            label="My Plans" 
            icon={<Home />} 
          />
          <BottomNavigationAction 
            label="Profile" 
            icon={<Person />} 
          />
        </BottomNavigation>
      </Paper>
    </Box>
  );
};

export default MainLayout; 