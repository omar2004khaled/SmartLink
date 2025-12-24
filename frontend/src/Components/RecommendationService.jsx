import apiClient from "../FetchData/FetchData";
export class RecommendationService {
  static async getJobSeekerRecommendations(userId) {
    try {
      const response = await apiClient.get(`/gemini/getRelevantJobs/${userId}`);
      if (response.data && Array.isArray(response.data)) {
        return response.data;
      }
      return [];
    } catch (error) {
      console.error('Error fetching recommendations:', error);
      return [];
    }
  }

  static checkRecommendationStatus(userId) {
    const status = localStorage.getItem(`recommendation_status_${userId}`);
    return status ? JSON.parse(status) : { 
      status: 'not_started', 
      data: [],
      timestamp: null 
    };
  }

  static setRecommendationStatus(userId, status, data = null) {
    localStorage.setItem(`recommendation_status_${userId}`, JSON.stringify({
      status,
      data,
      timestamp: new Date().toISOString()
    }));
  }
  static clearRecommendationStatus(userId) {
    localStorage.removeItem(`recommendation_status_${userId}`);
  }

  static async checkIfRecommendationsReady(userId) {
    try {
      const data = await this.getJobSeekerRecommendations(userId);
      
      if (data && data.length > 0) {
        this.setRecommendationStatus(userId, 'ready', data);
        return { status: 'ready', data };
      } else {
        this.setRecommendationStatus(userId, 'pending', []);
        return { status: 'pending', data: [] };
      }
    } catch (error) {
      this.setRecommendationStatus(userId, 'error', []);
      return { status: 'error', data: [] };
    }
  }
}
