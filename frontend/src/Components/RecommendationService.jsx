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
  static clearRecommendationStatus(userId) {
    localStorage.removeItem(`recommendation_status_${userId}`);
  }
}
