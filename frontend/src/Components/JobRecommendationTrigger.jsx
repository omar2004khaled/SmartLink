import { useEffect } from 'react';
import { RecommendationService } from '../services/RecommendationService';

const JobRecommendationTrigger = ({ userId }) => {
  useEffect(() => {
    const triggerRecommendations = async () => {
      if (!userId) return;
      
      const status = RecommendationService.checkRecommendationStatus(userId);
      
      // Only trigger if not already generating or generated recently (last hour)
      const shouldTrigger = status.status === 'not_started' || 
        (status.timestamp && Date.now() - new Date(status.timestamp).getTime() > 3600000);
      
      if (shouldTrigger) {
        console.log('Triggering recommendation generation for user:', userId);
        RecommendationService.setRecommendationStatus(userId, 'generating');
        
        // Trigger in background without blocking UI
        setTimeout(async () => {
          try {
            const success = await RecommendationService.triggerRecommendationGeneration(userId);
            if (success) {
              RecommendationService.setRecommendationStatus(userId, 'pending');
              
              // Poll for results (optional - can be done when user clicks button)
              pollForRecommendations(userId);
            }
          } catch (error) {
            RecommendationService.setRecommendationStatus(userId, 'failed');
          }
        }, 1000);
      }
    };

    const pollForRecommendations = async (userId) => {
      let attempts = 0;
      const maxAttempts = 60; // Poll for up to 5 minutes (60 * 5 seconds)
      
      const pollInterval = setInterval(async () => {
        attempts++;
        
        try {
          const recommendations = await RecommendationService.getJobSeekerRecommendations(userId);
          
          if (recommendations && recommendations.length > 0) {
            RecommendationService.setRecommendationStatus(userId, 'ready', recommendations);
            clearInterval(pollInterval);
            console.log('Recommendations ready!');
          } else if (attempts >= maxAttempts) {
            RecommendationService.setRecommendationStatus(userId, 'timeout');
            clearInterval(pollInterval);
          }
        } catch (error) {
          console.error('Polling error:', error);
          if (attempts >= maxAttempts) {
            RecommendationService.setRecommendationStatus(userId, 'error');
            clearInterval(pollInterval);
          }
        }
      }, 5000); // Poll every 5 seconds
    };

    triggerRecommendations();
  }, [userId]);

  return null; // This component doesn't render anything
};

export default JobRecommendationTrigger;