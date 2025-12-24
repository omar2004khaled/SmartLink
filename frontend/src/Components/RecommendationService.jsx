export class RecommendationService {
  static async getJobSeekerRecommendations(userId) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/recommendations/jobseeker/${userId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      });

      if (response.ok) {
        return await response.json();
      }
      return null;
    } catch (error) {
      console.error('Error fetching recommendations:', error);
      return null;
    }
  }

  static async triggerRecommendationGeneration(userId) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/recommendations/generate/${userId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      });

      if (response.ok) {
        console.log('Recommendation generation triggered successfully');
        return true;
      }
      return false;
    } catch (error) {
      console.error('Error triggering recommendation generation:', error);
      return false;
    }
  }

  static checkRecommendationStatus(userId) {
    const status = localStorage.getItem(`recommendation_status_${userId}`);
    return status ? JSON.parse(status) : { status: 'not_started', data: null };
  }

  static setRecommendationStatus(userId, status, data = null) {
    localStorage.setItem(`recommendation_status_${userId}`, JSON.stringify({
      status,
      data,
      timestamp: new Date().toISOString()
    }));
  }
}