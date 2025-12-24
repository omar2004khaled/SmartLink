import React, { useState, useEffect } from 'react';
import { Sparkles, Loader2, AlertCircle } from 'lucide-react';
import { RecommendationService } from './RecommendationService';
import './RecommendJobButton.css';

const RecommendJobButton = ({ userId }) => {
  const [showRecommendations, setShowRecommendations] = useState(false);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasChecked, setHasChecked] = useState(false);

  const handleButtonClick = async () => {
    if (!userId) {
      console.error('No user ID available');
      return;
    }

    setLoading(true);
    setHasChecked(true);
    
    try {
      console.log('Fetching recommendations from backend...');
      const data = await RecommendationService.getJobSeekerRecommendations(userId);
      console.log('Received recommendations:', data);
      
      setRecommendations(data || []);
      
      // Show modal regardless of whether we have data or not
      setShowRecommendations(true);
      
    } catch (error) {
      console.error('Failed to fetch recommendations:', error);
      setRecommendations([]);
      setShowRecommendations(true);
    } finally {
      setLoading(false);
    }
  };

  const getButtonText = () => {
    if (loading) return 'Checking...';
    return 'Recommend a Job';
  };

  return (
    <>
      <button
        onClick={handleButtonClick}
        disabled={loading}
        className="recommend-job-button"
      >
        {loading ? (
          <>
            <Loader2 className="animate-spin" size={16} />
            <span>Checking...</span>
          </>
        ) : (
          <>
            <Sparkles size={16} />
            <span>{getButtonText()}</span>
          </>
        )}
      </button>

      {showRecommendations && (
        <div className="recommendations-modal-overlay" onClick={() => setShowRecommendations(false)}>
          <div className="recommendations-modal" onClick={(e) => e.stopPropagation()}>
            <div className="recommendations-header">
              <h3>Recommended Jobs For You</h3>
              <button 
                className="close-button"
                onClick={() => setShowRecommendations(false)}
              >
                ✕
              </button>
            </div>
            
            <div className="recommendations-content">
              {recommendations.length > 0 ? (
                <div className="recommendations-list">
                  <p className="recommendations-count">
                    Found {recommendations.length} job{recommendations.length !== 1 ? 's' : ''} for you
                  </p>
                  
                  {recommendations.map((job, index) => (
                    <div key={index} className="job-recommendation-card">
                      <div className="job-header">
                        <h4>{job.title || job.jobTitle || `Job ${index + 1}`}</h4>
                        {job.company && <span className="company-name">{job.company}</span>}
                      </div>
                      
                      {job.description && (
                        <p className="job-description">
                          {job.description.length > 200 
                            ? `${job.description.substring(0, 200)}...` 
                            : job.description}
                        </p>
                      )}
                      
                      <div className="job-details">
                        {job.salary && (
                          <span className="salary-badge">${job.salary}</span>
                        )}
                        {job.location && (
                          <span className="location-badge">{job.location}</span>
                        )}
                        {job.matchScore && (
                          <span className="match-badge">
                            Match: {job.matchScore}%
                          </span>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="no-recommendations">
                  <AlertCircle size={48} />
                  <p>No recommendations available yet.</p>
                  <p className="small-text">
                    {hasChecked 
                      ? "We're still generating personalized recommendations for you. Please try again in a few moments."
                      : "Click the button to check for recommendations"}
                  </p>
                </div>
              )}
            </div>
            
            <div className="recommendations-footer">
              <button 
                className="refresh-button"
                onClick={() => {
                  setShowRecommendations(false);
                  // Small delay before fetching again
                  setTimeout(() => handleButtonClick(), 300);
                }}
              >
                <Loader2 size={16} />
                Check Again
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default RecommendJobButton;