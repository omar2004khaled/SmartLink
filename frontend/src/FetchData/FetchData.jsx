import axios from 'axios';
const API_BASE_URL = "http://localhost:8080";
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000
});
export const SavePost = async (postData) => {
    try {
        const response = await apiClient.post('/Post/add', postData);
        console.log("Post saved successfully:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error saving post:", error);
        if (error.code === 'ERR_NETWORK') {
            throw new Error('Unable to connect to server. Please check if the backend is running.');
        }
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
                ascending: ascending
            }
        });
        console.log(`Posts fetched successfully (page ${page}, size ${size}):`, response.data);
        return response.data;
    } catch (error) {
        console.error("Error fetching posts:", error);
        throw error;
    }
}

// For getting all posts at once (using large page size)
export const GetAllPosts = async () => {
    return await GetPosts(0, 1000, 'PostId', false);
}

export const GetSpecificPost = async (PostId) => {
    try {
        const response = await apiClient.get(`/Post/${PostId}`);
        console.log("Post with id " + PostId + " fetched successfully:", response.data);
        return response.data;
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

// Health check function to test backend connectivity
export const TestBackendConnection = async () => {
    try {
        const response = await apiClient.get('/api/health');
        console.log("Backend connection test successful:", response.data);
        return response.data;
    } catch (error) {
        console.error("Backend connection test failed:", error);
        throw error;
    }
}