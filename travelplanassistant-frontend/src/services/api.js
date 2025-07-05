import { API_CONFIG, STORAGE_KEYS, ERROR_MESSAGES } from '../config/config';

// Helper function to get auth token
const getAuthToken = () => {
  return localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN);
};

// Helper function to get auth headers
const getAuthHeaders = () => {
  const token = getAuthToken();
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` })
  };
};

// Generic API request function with timeout and retry logic
const apiRequest = async (endpoint, options = {}) => {
  const url = `${API_CONFIG.BASE_URL}${endpoint}`;
  const config = {
    headers: getAuthHeaders(),
    ...options
  };

  let lastError;
  
  for (let attempt = 1; attempt <= API_CONFIG.RETRY_ATTEMPTS; attempt++) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
      
      const response = await fetch(url, {
        ...config,
        signal: controller.signal
      });
      
      clearTimeout(timeoutId);
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        
        // Handle specific HTTP status codes
        switch (response.status) {
          case 401:
            localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
            throw new Error(ERROR_MESSAGES.UNAUTHORIZED);
          case 404:
            throw new Error(ERROR_MESSAGES.NOT_FOUND);
          case 422:
            throw new Error(errorData.message || ERROR_MESSAGES.VALIDATION_ERROR);
          case 500:
            throw new Error(ERROR_MESSAGES.SERVER_ERROR);
          default:
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
      }
      
      return await response.json();
    } catch (error) {
      lastError = error;
      
      // Don't retry on client errors (4xx) except 429 (rate limit)
      if (error.name === 'AbortError') {
        throw new Error(ERROR_MESSAGES.TIMEOUT_ERROR);
      }
      
      if (attempt === API_CONFIG.RETRY_ATTEMPTS) {
        if (error.message.includes('Failed to fetch')) {
          throw new Error(ERROR_MESSAGES.NETWORK_ERROR);
        }
        throw error;
      }
      
      // Wait before retrying (exponential backoff)
      await new Promise(resolve => setTimeout(resolve, Math.pow(2, attempt) * 1000));
    }
  }
  
  throw lastError;
};

// Authentication API
export const authAPI = {
  // Register new user
  register: async (userData) => {
    return apiRequest('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData)
    });
  },

  // Login user
  login: async (credentials) => {
    return apiRequest('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials)
    });
  },

  // Get current user profile
  getCurrentUser: async () => {
    return apiRequest('/auth/me');
  }
};

// Travel Plans API
export const travelPlansAPI = {
  // Get all travel plans (for discovery)
  getAllPlans: async (filters = {}) => {
    const queryParams = new URLSearchParams(filters).toString();
    const endpoint = queryParams ? `/travel-plans?${queryParams}` : '/travel-plans';
    return apiRequest(endpoint);
  },

  // Get user's travel plans
  getUserPlans: async () => {
    return apiRequest('/travel-plans/my-plans');
  },

  // Get specific travel plan
  getPlan: async (planId) => {
    return apiRequest(`/travel-plans/${planId}`);
  },

  // Create new travel plan
  createPlan: async (planData) => {
    return apiRequest('/travel-plans', {
      method: 'POST',
      body: JSON.stringify(planData)
    });
  },

  // Update travel plan
  updatePlan: async (planId, planData) => {
    return apiRequest(`/travel-plans/${planId}`, {
      method: 'PUT',
      body: JSON.stringify(planData)
    });
  },

  // Delete travel plan
  deletePlan: async (planId) => {
    return apiRequest(`/travel-plans/${planId}`, {
      method: 'DELETE'
    });
  },

  // Apply to travel plan
  applyToPlan: async (planId) => {
    return apiRequest(`/travel-plans/${planId}/apply`, {
      method: 'POST'
    });
  },

  // Cancel application
  cancelApplication: async (planId) => {
    return apiRequest(`/travel-plans/${planId}/cancel-application`, {
      method: 'POST'
    });
  },

  // Accept application
  acceptApplication: async (planId, userId) => {
    return apiRequest(`/travel-plans/${planId}/accept-application`, {
      method: 'POST',
      body: JSON.stringify({ userId })
    });
  },

  // Refuse application
  refuseApplication: async (planId, userId) => {
    return apiRequest(`/travel-plans/${planId}/refuse-application`, {
      method: 'POST',
      body: JSON.stringify({ userId })
    });
  },

  // Invite user to plan
  inviteUser: async (planId, userEmail) => {
    return apiRequest(`/travel-plans/${planId}/invite`, {
      method: 'POST',
      body: JSON.stringify({ email: userEmail })
    });
  },

  // Accept invitation
  acceptInvitation: async (planId) => {
    return apiRequest(`/travel-plans/${planId}/accept-invitation`, {
      method: 'POST'
    });
  },

  // Refuse invitation
  refuseInvitation: async (planId) => {
    return apiRequest(`/travel-plans/${planId}/refuse-invitation`, {
      method: 'POST'
    });
  }
};

// Chat API
export const chatAPI = {
  // Get chat messages for a plan
  getMessages: async (planId) => {
    return apiRequest(`/chat/${planId}/messages`);
  },

  // Send message
  sendMessage: async (planId, message) => {
    return apiRequest(`/chat/${planId}/messages`, {
      method: 'POST',
      body: JSON.stringify({ content: message })
    });
  }
};

// Polls API
export const pollsAPI = {
  // Get polls for a plan
  getPolls: async (planId) => {
    return apiRequest(`/polls/${planId}`);
  },

  // Create new poll
  createPoll: async (pollData) => {
    return apiRequest('/polls', {
      method: 'POST',
      body: JSON.stringify(pollData)
    });
  },

  // Vote on poll
  vote: async (pollId, optionIndex) => {
    return apiRequest(`/polls/${pollId}/vote`, {
      method: 'POST',
      body: JSON.stringify({ optionIndex })
    });
  }
};

// Expenses API
export const expensesAPI = {
  // Get expenses for a plan
  getExpenses: async (planId) => {
    return apiRequest(`/expenses/${planId}`);
  },

  // Create new expense
  createExpense: async (expenseData) => {
    return apiRequest('/expenses', {
      method: 'POST',
      body: JSON.stringify(expenseData)
    });
  },

  // Mark expense as settled
  settleExpense: async (expenseId) => {
    return apiRequest(`/expenses/${expenseId}/settle`, {
      method: 'PUT'
    });
  }
};

// Users API
export const usersAPI = {
  // Get user profile
  getProfile: async (userId) => {
    return apiRequest(`/users/${userId}`);
  },

  // Update user profile
  updateProfile: async (userData) => {
    return apiRequest('/users/profile', {
      method: 'PUT',
      body: JSON.stringify(userData)
    });
  },

  // Search users
  searchUsers: async (query) => {
    return apiRequest(`/users/search?q=${encodeURIComponent(query)}`);
  }
};

// WebSocket connection for real-time chat
export const createWebSocketConnection = (planId, onMessage) => {
  const token = getAuthToken();
  const wsUrl = `ws://localhost:8080/api/ws/chat/${planId}?token=${token}`;
  
  const ws = new WebSocket(wsUrl);
  
  ws.onopen = () => {
    console.log('WebSocket connected');
  };
  
  ws.onmessage = (event) => {
    const message = JSON.parse(event.data);
    onMessage(message);
  };
  
  ws.onerror = (error) => {
    console.error('WebSocket error:', error);
  };
  
  ws.onclose = () => {
    console.log('WebSocket disconnected');
  };
  
  return ws;
};

export default {
  authAPI,
  travelPlansAPI,
  chatAPI,
  pollsAPI,
  expensesAPI,
  usersAPI,
  createWebSocketConnection
}; 