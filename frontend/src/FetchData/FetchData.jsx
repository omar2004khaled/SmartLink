import axios from 'axios';
const API_BASE_URL = "http://localhost:8080";
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: false, // Set to true if you need cookies
    headers: {
        'Content-Type': 'application/json',
    }
});
export const SavePost = async (postData) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/Post/add`, postData);
        console.log("Post saved successfully:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error saving post:", error);
        throw error;
    }
}
export const GetPosts = async (page = 0, size = 10, sortBy = 'postId', ascending = false) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/Post/all`, {
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
        const response = await axios.get(`${API_BASE_URL}/Post/${PostId}`);
        console.log("Post with id " + PostId + " fetched successfully:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error fetching Post:", error);
        throw error;
    }
}
export const DeletePost = async (PostId) => {
    try {
        const response = await axios.delete(`${API_BASE_URL}/Post/delete/${PostId}`);
        console.log("Post with id " + PostId + " deleted successfully:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error deleting Post:", error);
        throw error;
    }
}
export const UpdatePost = async (PostId, updatedData) => {
    try {
        const response = await axios.put(`${API_BASE_URL}/Post/update/${PostId}`, updatedData);
        console.log("Post with id " + PostId + " updated successfully:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error updating Post:", error);
        throw error;
    }
}