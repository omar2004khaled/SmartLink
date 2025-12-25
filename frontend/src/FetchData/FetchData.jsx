import axios from 'axios';
import { API_BASE_URL } from '../config';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true, // CRITICAL: Set to true for CORS to work properly
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000, // 10 second timeout
});

// Request interceptor to add JWT token if available
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`, config.data || config.params);
        return config;
    },
    (error) => {
        console.error('Request interceptor error:', error);
        return Promise.reject(error);
    }
);
apiClient.interceptors.response.use(
    (response) => {
        console.log(`API Response: ${response.config.method?.toUpperCase()} ${response.config.url}`, response.data);
        return response;
    },
    (error) => {
        if (error.response) {
            // Server responded with error status
            console.error(`API Error [${error.response.status}]:`, error.response.data);
            
            // Handle specific status codes
            if (error.response.status === 401) {
                console.warn('Unauthorized - consider redirecting to login');
            } else if (error.response.status === 403) {
                console.warn('Forbidden - insufficient permissions');
            } else if (error.response.status === 404) {
                console.warn('Resource not found');
            }
        } else if (error.request) {
            // Request made but no response received
            console.error('Network Error - No response from server:', error.message);
            console.error('Please check if backend is running on', API_BASE_URL);
        } else {
            // Error setting up the request
            console.error('Request setup error:', error.message);
        }
        return Promise.reject(error);
    }
);
export const userIdFromLocalStorage = () => {
    const userId = localStorage.getItem('userId');
    return userId ? parseInt(userId) : null;
};
export const SavePost = async (postData) => {
    try {
        const response = await apiClient.post('/Post/add', postData);
        console.log("Post saved successfully:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error saving post:", error);
        throw error;
    }
}
export const GetPosts = async (page = 0, size = 10, sortBy = 'postId', ascending = false) => {
    try {
      
        const response = await apiClient.get('/Post/all', {
            params: {
                page: page,
                size: size,
                sortBy: sortBy,
                ascending: ascending,
                viewerId: userIdFromLocalStorage()
            }
        });
        console.log(`Posts fetched successfully (page ${page}, size ${size}):`, response.data);
        
        const normalizedData = response.data.map(post => ({
            ...post,
            id: post.id || post.postId, 
        }));
        
        return normalizedData;
    } catch (error) {
        console.error("Error fetching posts:", error);
        throw error;
    }
}

// For getting all posts at once (using large page size)
export const GetAllPosts = async () => {
    return await GetPosts(0, 1000, 'postId', false);
}

export const GetSpecificPost = async (PostId) => {
    try {
        const response = await apiClient.get(`/Post/${PostId}`);
        console.log("Post with id " + PostId + " fetched successfully:", response.data);
        
        const post = response.data;
        return {
            ...post,
            id: post.id || post.postId,
        };
    } catch (error) {
        console.error("Error fetching Post:", error);
        throw error;
    }
}
export const DeletePost = async (PostId) => {
    try {
        const response = await apiClient.delete(`/Post/delete/${PostId}`);
        console.log("Post with id " + PostId + " deleted successfully:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error deleting Post:", error);
        throw error;
    }
}
export const UpdatePost = async (PostId, updatedData) => {
    try {
        const response = await apiClient.put(`/Post/update/${PostId}`, updatedData);
        console.log("Post with id " + PostId + " updated successfully:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error updating Post:", error);
        throw error;
    }
}
export const GetComments = async (postId, page = 0, size = 10) => {
    try {
        console.log(`Fetching comments for post ${postId}, page ${page}`);
        const response = await apiClient.get(`/comment/getAll/${postId}/${page}`);
        console.log(`Comments for post ${postId} fetched:`, response.data);
        return response.data;
    } catch (error) {
        console.error("Error fetching comments:", error);
        throw error;
    }
}

export const SaveComment = async (commentDTO) => {
    try {
        console.log("=== Attempting to save comment ===");
        console.log("Comment data:", commentDTO);
        
        if (!commentDTO.postId) {
            throw new Error('Post ID is required to save a comment');
        }
        
        console.log("API endpoint:", `${API_BASE_URL}/comment/add`);
        
        const response = await apiClient.post('/comment/add', commentDTO);
        
        console.log("=== Comment saved successfully ===");
        console.log("Response:", response.data);
        return response.data;
    } catch (error) {
        console.error("=== Error saving comment ===");
        
        if (error.response) {
            console.error("Status:", error.response.status);
            console.error("Data:", error.response.data);
            console.error("Headers:", error.response.headers);
            
            if (error.response.status === 403) {
                throw new Error('Access denied. Please check your permissions.');
            } else if (error.response.status === 401) {
                throw new Error('Unauthorized. Please log in again.');
            } else if (error.response.status === 400) {
                throw new Error(`Invalid data: ${error.response.data.message || 'Please check your input'}`);
            } else if (error.response.status === 500) {
                throw new Error('Server error. Please try again later.');
            }
        } else if (error.request) {
            console.error("No response received from server");
            console.error("Request:", error.request);
            throw new Error('Cannot connect to server. Please check if the backend is running.');
        } else {
            console.error("Error:", error.message);
        }
        throw error;
    }
}

export const GetUserInfo = async (userId) => {
    try {
        console.log(`Fetching user info for userId: ${userId}`);
        const response = await apiClient.get(`/auth/user/${userId}`);
        console.log(`User info for userId ${userId} fetched:`, response.data);
        return response.data;
    } catch (error) {
        console.error(`Error fetching user info for userId ${userId}:`, error);
        if (error.response) {
            console.error(`Status: ${error.response.status}, Data:`, error.response.data);
        }
        return null;
    }
}
// Add to FetchData.jsx

export const toggleReaction = async (postId, userId, reactionType) => {
  try {
    const response = await apiClient.post('/reactions/toggle', null, {
      params: {
        postId,
        userId,
        reactionType
      }
    });
    console.log("Reaction toggled:", response.data);
    return response.data;
  } catch (error) {
    console.error("Error toggling reaction:", error);
    throw error;
  }
};

export const getReactionCounts = async (postId) => {
  try {
    const response = await apiClient.get(`/reactions/count/${postId}`);
    console.log(`Reaction counts for post ${postId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error("Error fetching reaction counts:", error);
    throw error;
  }
};

export const getTopReactions = async (postId, limit = 3) => {
  try {
    const response = await apiClient.get(`/reactions/top/${postId}`, {
      params: { limit }
    });
    console.log(`Top ${limit} reactions for post ${postId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error("Error fetching top reactions:", error);
    throw error;
  }
};

export const getTotalReactions = async (postId) => {
  try {
    const response = await apiClient.get(`/reactions/total/${postId}`);
    console.log(`Total reactions for post ${postId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error("Error fetching total reactions:", error);
    throw error;
  }
};

export const getUserReaction = async (postId, userId) => {
  try {
    const response = await apiClient.get('/reactions/user', {
      params: { postId, userId }
    });
    console.log(`User reaction for post ${postId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error("Error fetching user reaction:", error);
    throw error;
  }
};

export const reportPost = async (postId, reporterId, category, description = '') => {
  try {
    const response = await apiClient.post(`/Post/${postId}/report`, null, {
      params: {
        reporterId,
        category,
        description
      }
    });
    console.log("Post reported successfully:", response.data);
    return response.data;
  } catch (error) {
    console.error("Error reporting post:", error);
    throw error;
  }
};

export const getReportsForPost = async (postId) => {
  try {
    const response = await apiClient.get(`/Post/${postId}/reports`);
    console.log(`Reports for post ${postId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error("Error fetching reports:", error);
    throw error;
  }
};

export const getReportCount = async (postId) => {
  try {
    const response = await apiClient.get(`/Post/${postId}/report-count`);
    console.log(`Report count for post ${postId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error("Error fetching report count:", error);
    throw error;
  }
};

export const hasUserReported = async (postId, reporterId) => {
  try {
    const response = await apiClient.get(`/Post/${postId}/has-reported`, {
      params: { reporterId }
    });
    console.log(`User ${reporterId} has reported post ${postId}:`, response.data);
    return response.data;
  } catch (error) {
    console.error("Error checking if user reported:", error);
    throw error;
  }
};

export default apiClient;