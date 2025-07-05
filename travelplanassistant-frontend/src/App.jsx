import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Welcome from './pages/Welcome';
import Login from './pages/Login';
import Register from './pages/Register';
import MainLayout from './components/MainLayout';
import Discovery from './pages/Discovery';
import MyPlans from './pages/MyPlans';
import Profile from './pages/Profile';
import PlanDetails from './pages/PlanDetails';
import UserProfile from './pages/UserProfile';
import CreatePlan from './pages/CreatePlan';
import ChatRoom from './pages/ChatRoom';
import Poll from './pages/Poll';
import CreatePoll from './pages/CreatePoll';
import SharedExpense from './pages/SharedExpense';
import CreateSharedExpense from './pages/CreateSharedExpense';
import EditProfile from './pages/EditProfile';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/" replace />;
};

const AppRoutes = () => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return (
      <Routes>
        <Route path="/" element={<Welcome />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    );
  }

  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Navigate to="/discovery" replace />} />
        <Route path="discovery" element={<Discovery />} />
        <Route path="my-plans" element={<MyPlans />} />
        <Route path="profile" element={<Profile />} />
        <Route path="plan/:planId" element={<PlanDetails />} />
        <Route path="user/:userId" element={<UserProfile />} />
        <Route path="create-plan" element={<CreatePlan />} />
        <Route path="chat/:planId" element={<ChatRoom />} />
        <Route path="poll/:planId" element={<Poll />} />
        <Route path="create-poll/:planId" element={<CreatePoll />} />
        <Route path="expense/:planId" element={<SharedExpense />} />
        <Route path="create-expense/:planId" element={<CreateSharedExpense />} />
        <Route path="edit-profile" element={<EditProfile />} />
      </Route>
    </Routes>
  );
};

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <AppRoutes />
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
