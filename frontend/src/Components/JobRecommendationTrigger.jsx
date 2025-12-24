import { useEffect } from 'react';
import { RecommendationService } from './RecommendationService';

const JobRecommendationTrigger = ({ userId }) => {
  useEffect(() => {
    if (!userId) return;

    // Just check if we already have recommendations stored
    const checkExistingRecommendations = async () => {
      const status = RecommendationService.checkRecommendationStatus(userId);
      
      // If not checked before or checked more than 1 hour ago, check again
      if (!status.timestamp || Date.now() - new Date(status.timestamp).getTime() > 3600000) {
        try {
          const result = await RecommendationService.checkIfRecommendationsReady(userId);
          console.log('Recommendation check result:', result.status);
        } catch (error) {
          console.error('Error checking recommendations:', error);
        }
      }
    };

    // Wait a bit after component mounts to avoid blocking initial render
    const timer = setTimeout(checkExistingRecommendations, 2000);
    
    return () => clearTimeout(timer);
  }, [userId]);

  return null;
};

export default JobRecommendationTrigger;