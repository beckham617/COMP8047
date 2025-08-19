import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authAPI } from '../services/api';
import { STORAGE_KEYS } from '../config/config';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  // Check if user is already logged in on app start
  useEffect(() => {
    checkAuthStatus();
    // eslint-disable-next-line
  }, []);

  // Expose checkAuthStatus for manual reloads (memoized to prevent unnecessary re-renders)
  const checkAuthStatus = useCallback(async () => {
    setLoading(true);
    const token = localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
    if (token) {
      try {
        const userData = await authAPI.getCurrentUser();
        setUser(userData);
        setIsAuthenticated(true);
      } catch (error) {
        console.error('Failed to get current user:', error);
        localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
        setUser(null);
        setIsAuthenticated(false);
      }
    } else {
      setUser(null);
      setIsAuthenticated(false);
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    try {
      const response = await authAPI.login({ email, password });
      
      // Store token
      localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, response.token);
      
      // Set user data
      setUser({
        id: response.userId || response.id,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        gender: response.gender,
        birthYear: response.birthYear,
        birthMonth: response.birthMonth,
        phoneNumber: response.phoneNumber,
        language: response.language,
        country: response.country,
        city: response.city,
        profilePicture: response.profilePicture || 'https://via.placeholder.com/150',
        bio: response.bio,
        createdAt: response.createdAt
      });
      
      setIsAuthenticated(true);
      return { success: true };
    } catch (error) {
      console.error('Login failed:', error);
      return { 
        success: false, 
        error: error.message || 'Login failed. Please check your credentials.' 
      };
    }
  };

  const register = async (userData) => {
    try {
      const response = await authAPI.register(userData);
      
      // Store token
      localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, response.token);
      
      // Set user data
      setUser({
        id: response.userId || response.id,
        email: response.email,
        firstName: response.firstName,
        lastName: response.lastName,
        gender: response.gender,
        birthYear: response.birthYear,
        birthMonth: response.birthMonth,
        phoneNumber: response.phoneNumber,
        language: response.language,
        country: response.country,
        city: response.city,
        profilePicture: response.profilePicture || 'https://via.placeholder.com/150',
        bio: response.bio,
        createdAt: response.createdAt
      });
      
      setIsAuthenticated(true);
      return { success: true };
    } catch (error) {
      console.error('Registration failed:', error);
      return { 
        success: false, 
        error: error.message || 'Registration failed. Please try again.' 
      };
    }
  };

  const logout = () => {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    setUser(null);
    setIsAuthenticated(false);
  };

  const updateUser = (updatedUserData) => {
    setUser(prevUser => ({
      ...prevUser,
      ...updatedUserData
    }));
  };

  const value = {
    user,
    isAuthenticated,
    loading,
    login,
    register,
    logout,
    updateUser,
    reloadUser: checkAuthStatus
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 