// authService.js
// Authentication service for managing user auth state

const TOKEN_KEY = 'authToken';
const USER_INFO_KEY = 'userInfo';
const API_BASE_URL = 'http://localhost:8080';

/**
 * Check if user is authenticated by verifying token exists
 * @returns {boolean} - True if token exists
 */
export const isAuthenticated = () => {
  const token = localStorage.getItem(TOKEN_KEY);
  return !!token;
};

/**
 * Get the authentication token from localStorage
 * @returns {string|null} - The auth token or null
 */
export const getAuthToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

/**
 * Set the authentication token in localStorage
 * @param {string} token - JWT token to store
 */
export const setAuthToken = (token) => {
  localStorage.setItem(TOKEN_KEY, token);
};

/**
 * Check if user info exists in localStorage
 * @returns {boolean} - True if user info exists
 */
export const hasUserInfo = () => {
  const userInfo = localStorage.getItem(USER_INFO_KEY);
  return !!userInfo;
};

/**
 * Get user info from localStorage
 * @returns {object|null} - User info object or null
 */
export const getUserInfoFromStorage = () => {
  const userInfo = localStorage.getItem(USER_INFO_KEY);
  return userInfo ? JSON.parse(userInfo) : null;
};

/**
 * Set user info in localStorage
 * @param {object} userInfo - User information object
 */
export const setUserInfo = (userInfo) => {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
};

/**
 * Fetch user info from backend API
 * @param {string} token - JWT token for authentication
 * @returns {Promise<object>} - User info object
 * @throws {Error} - If fetch fails or token is invalid
 */
export const getUserInfo = async (token) => {
  if (!token) {
    throw new Error('No token provided');
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/user/me`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      // If 401, token is invalid/expired
      if (response.status === 401) {
        throw new Error('Token expired or invalid');
      }
      throw new Error(`Failed to fetch user info: ${response.status}`);
    }

    const userInfo = await response.json();
    
    // Store user info in localStorage
    setUserInfo(userInfo);
    
    return userInfo;
  } catch (error) {
    console.error('Error fetching user info:', error);
    throw error;
  }
};

/**
 * Logout user by clearing all auth data
 */
export const logout = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_INFO_KEY);
  // Clear any other user-related data if needed
};

/**
 * Login user with credentials
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise<object>} - Login response with token and user info
 * @throws {Error} - If login fails
 */
export const login = async (email, password) => {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify({
        email: email.toLowerCase().trim(),
        password: password,
      }),
    });

    const responseText = await response.text();
    let data;
    
    try {
      data = JSON.parse(responseText);
    } catch (e) {
      data = { message: responseText || 'No response from server' };
    }

    if (!response.ok) {
      let errorMessage = 'Login failed';
      
      if (typeof data === 'string') {
        errorMessage = data;
      } else if (data.message) {
        errorMessage = data.message;
      } else if (data.error) {
        errorMessage = data.error;
      }

      if (response.status === 401) {
        errorMessage = 'Invalid email or password';
      } else if (response.status === 403) {
        errorMessage = 'Account not verified. Please check your email';
      }

      throw new Error(errorMessage);
    }

    // Store token
    if (data.token) {
      setAuthToken(data.token);
      
      // If user info is returned, store it
      if (data.user) {
        setUserInfo(data.user);
      } else {
        // Fetch user info using the token
        await getUserInfo(data.token);
      }
    }

    return data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
};

/**
 * Register new user
 * @param {object} userData - User registration data
 * @returns {Promise<object>} - Registration response
 * @throws {Error} - If registration fails
 */
export const register = async (userData) => {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
      body: JSON.stringify(userData),
    });

    const responseText = await response.text();
    let data;
    
    try {
      data = JSON.parse(responseText);
    } catch (e) {
      data = { message: responseText || 'No response from server' };
    }

    if (!response.ok) {
      let errorMessage = 'Registration failed';
      
      if (typeof data === 'string') {
        errorMessage = data;
      } else if (data.message) {
        errorMessage = data.message;
      } else if (data.error) {
        errorMessage = data.error;
      }

      if (response.status === 409) {
        errorMessage = 'Email already registered';
      } else if (response.status === 400) {
        errorMessage = data.message || 'Invalid registration data';
      }

      throw new Error(errorMessage);
    }

    return data;
  } catch (error) {
    console.error('Registration error:', error);
    throw error;
  }
};

/**
 * Verify JWT token validity
 * @param {string} token - JWT token to verify
 * @returns {Promise<boolean>} - True if token is valid
 */
export const verifyToken = async (token) => {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/verify`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    return response.ok;
  } catch (error) {
    console.error('Token verification error:', error);
    return false;
  }
};

/**
 * Refresh authentication token
 * @returns {Promise<string>} - New token
 * @throws {Error} - If refresh fails
 */
export const refreshToken = async () => {
  const currentToken = getAuthToken();
  
  if (!currentToken) {
    throw new Error('No token to refresh');
  }

  try {
    const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${currentToken}`,
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error('Token refresh failed');
    }

    const data = await response.json();
    
    if (data.token) {
      setAuthToken(data.token);
      return data.token;
    }

    throw new Error('No token in refresh response');
  } catch (error) {
    console.error('Token refresh error:', error);
    logout(); // Clear invalid token
    throw error;
  }
};

/**
 * Get user ID from stored user info
 * @returns {number|null} - User ID or null
 */
export const getUserId = () => {
  const userInfo = getUserInfoFromStorage();
  return userInfo?.userId || userInfo?.id || null;
};

/**
 * Get user role from stored user info
 * @returns {string|null} - User role or null
 */
export const getUserRole = () => {
  const userInfo = getUserInfoFromStorage();
  return userInfo?.role || null;
};

/**
 * Check if user has specific role
 * @param {string} role - Role to check
 * @returns {boolean} - True if user has the role
 */
export const hasRole = (role) => {
  const userRole = getUserRole();
  return userRole === role;
};

export default {
  isAuthenticated,
  getAuthToken,
  setAuthToken,
  hasUserInfo,
  getUserInfoFromStorage,
  setUserInfo,
  getUserInfo,
  logout,
  login,
  register,
  verifyToken,
  refreshToken,
  getUserId,
  getUserRole,
  hasRole,
};