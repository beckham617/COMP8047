import React from 'react';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import SearchIcon from '@mui/icons-material/Search';
import HomeIcon from '@mui/icons-material/Home';
import PersonIcon from '@mui/icons-material/Person';

const BottomNav = ({ value, onChange }) => (
  <BottomNavigation
    value={value}
    onChange={onChange}
    showLabels
    sx={{ position: 'fixed', bottom: 0, left: 0, right: 0, zIndex: 1000 }}
  >
    <BottomNavigationAction label="Discovery" icon={<SearchIcon />} />
    <BottomNavigationAction label="My Plans" icon={<HomeIcon />} />
    <BottomNavigationAction label="Profile" icon={<PersonIcon />} />
  </BottomNavigation>
);

export default BottomNav; 