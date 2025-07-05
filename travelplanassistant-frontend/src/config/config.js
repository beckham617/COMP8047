// API Configuration
export const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  TIMEOUT: 10000, // 10 seconds
  RETRY_ATTEMPTS: 3
};

// WebSocket Configuration
export const WS_CONFIG = {
  BASE_URL: process.env.REACT_APP_WS_URL || 'ws://localhost:8080/api/ws',
  RECONNECT_INTERVAL: 5000, // 5 seconds
  MAX_RECONNECT_ATTEMPTS: 5
};

// App Configuration
export const APP_CONFIG = {
  NAME: 'Travel Plan Assistant',
  VERSION: '1.0.0',
  MAX_FILE_SIZE: 5 * 1024 * 1024, // 5MB
  SUPPORTED_IMAGE_TYPES: ['image/jpeg', 'image/png', 'image/gif'],
  MAX_PLAN_IMAGES: 5,
  MAX_POLL_OPTIONS: 10,
  DEFAULT_PLAN_VISIBILITY: 'PUBLIC',
  DEFAULT_MAX_MEMBERS: 4
};

// Feature Flags
export const FEATURES = {
  ENABLE_REAL_TIME_CHAT: true,
  ENABLE_POLLS: true,
  ENABLE_EXPENSES: true,
  ENABLE_EMAIL_NOTIFICATIONS: false, // Set to true when backend email is configured
  ENABLE_FILE_UPLOADS: true
};

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'authToken',
  USER_DATA: 'userData',
  PLANS: 'plans',
  CHAT_MESSAGES: 'chat_',
  POLLS: 'polls_',
  EXPENSES: 'expenses_'
};

// Error Messages
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error. Please check your connection.',
  UNAUTHORIZED: 'You are not authorized to perform this action.',
  NOT_FOUND: 'The requested resource was not found.',
  VALIDATION_ERROR: 'Please check your input and try again.',
  SERVER_ERROR: 'Server error. Please try again later.',
  TIMEOUT_ERROR: 'Request timed out. Please try again.',
  FILE_TOO_LARGE: 'File size is too large. Maximum size is 5MB.',
  UNSUPPORTED_FILE_TYPE: 'Unsupported file type. Please use JPEG, PNG, or GIF.',
  INVALID_CREDENTIALS: 'Invalid email or password.',
  EMAIL_ALREADY_EXISTS: 'An account with this email already exists.',
  USERNAME_ALREADY_EXISTS: 'This username is already taken.'
};

// Success Messages
export const SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'Successfully logged in!',
  REGISTER_SUCCESS: 'Account created successfully!',
  PLAN_CREATED: 'Travel plan created successfully!',
  PLAN_UPDATED: 'Travel plan updated successfully!',
  PLAN_DELETED: 'Travel plan deleted successfully!',
  APPLICATION_SENT: 'Application sent successfully!',
  APPLICATION_CANCELLED: 'Application cancelled successfully!',
  INVITATION_SENT: 'Invitation sent successfully!',
  MESSAGE_SENT: 'Message sent successfully!',
  POLL_CREATED: 'Poll created successfully!',
  VOTE_SUBMITTED: 'Vote submitted successfully!',
  EXPENSE_CREATED: 'Expense created successfully!',
  PROFILE_UPDATED: 'Profile updated successfully!'
};

export default {
  API_CONFIG,
  WS_CONFIG,
  APP_CONFIG,
  FEATURES,
  STORAGE_KEYS,
  ERROR_MESSAGES,
  SUCCESS_MESSAGES
}; 