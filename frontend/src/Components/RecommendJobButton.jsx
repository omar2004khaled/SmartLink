import React, { useState, useEffect } from 'react';
import { Sparkles, Loader2, Clock, AlertCircle } from 'lucide-react';
import { RecommendationService } from './RecommendationService';
import './RecommendJobButton.css';

const RecommendJobButton = ({ userId }) => {
  const [showRecommendations, setShowRecommendations] = useState(false);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState('not_started');
  const [isChecking, setIsChecking] = useState(false);

  useEffect(() => {
    // Check initial status on component mount
    checkRecommendations();
  }, [userId]);

  const checkRecommendations = async () => {
    if (!userId) return;
    
    const storedStatus = RecommendationService.checkRecommendationStatus(userId);
    setStatus(storedStatus.status);
    setRecommendations(storedStatus.data || []);
    
    // If status is not_started, we should check the backend
    if (storedStatus.status === 'not_started') {
      const result = await RecommendationService.checkIfRecommendationsReady(userId);
      setStatus(result.status);
      setRecommendations(result.data);
    }
  };

  const handleButtonClick = async () => {
    // If we have recommendations ready, show them
    if (status === 'ready' && recommendations.length > 0) {
      setShowRecommendations(true);
      return;
    }

    setLoading(true);
    
    try {
      // Check current status from backend
      const result = await RecommendationService.checkIfRecommendationsReady(userId);
      setStatus(result.status);
      setRecommendations(result.data);
      
      if (result.status === 'ready' && result.data.length > 0) {
        // Recommendations are ready, show them
        setShowRecommendations(true);
      } else {
        // Recommendations are not ready yet, start polling
        startPolling();
      }
    } catch (error) {
      console.error('Error checking recommendations:', error);
      setStatus('error');
    } finally {
      setLoading(false);
    }
  };

  const startPolling = () => {
    setIsChecking(true);
    pollForRecommendations();
  };

  const pollForRecommendations = async () => {
    let attempts = 0;
    const maxAttempts = 36; // Poll for up to 3 minutes (36 * 5 seconds)
    
    const poll = async () => {
      attempts++;
      
      try {
        console.log(`Polling attempt ${attempts}/${maxAttempts}`);
        const result = await RecommendationService.checkIfRecommendationsReady(userId);
        
        if (result.status === 'ready' && result.data.length > 0) {
          // Recommendations are ready
          setStatus('ready');
          setRecommendations(result.data);
          setIsChecking(false);
          setShowRecommendations(true);
          return true;
        }
        
        if (attempts >= maxAttempts) {
          // Timeout after 3 minutes
          setStatus('timeout');
          setIsChecking(false);
          return false;
        }
        
        // Continue polling every 5 seconds
        setTimeout(poll, 5000);
      } catch (error) {
        console.error('Polling error:', error);
        if (attempts >= maxAttempts) {
          setStatus('error');
          setIsChecking(false);
        } else {
          setTimeout(poll, 5000);
        }
      }
    };
    
    poll();
  };

  const getButtonText = () => {
    switch (status) {
      case 'ready':
        return `Recommended Jobs (${recommendations.length})`;
      case 'pending':
        return 'Generating Recommendations...';
      case 'error':
        return 'Recommendation Error';
      case 'timeout':
        return 'Generating (Taking time...)';
      default:
        return 'Recommend a Job';
    }
  };

  const handleRefreshRecommendations = () => {
    // Clear existing recommendations and start fresh
    RecommendationService.clearRecommendationStatus(userId);
    setStatus('not_started');
    setRecommendations([]);
    setShowRecommendations(false);
    
    // The backend endpoint will generate new recommendations when called
    handleButtonClick();
  };

  return (
    <>
      <button
        onClick={handleButtonClick}
        disabled={loading || isChecking}
        className="recommend-job-button"
      >
        {loading || isChecking ? (
          <>
            <Loader2 className="animate-spin" size={16} />
            <span>{isChecking ? 'Checking...' : 'Loading...'}</span>
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
              {status === 'ready' && recommendations.length > 0 ? (
                <div className="recommendations-list">
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
                      
                      {/* <div className="job-actions">
                        <button className="apply-button">View Details</button>
                        <button className="save-button">Save</button>
                      </div> */}
                    </div>
                  ))}
                </div>
              ) : (
                <div className="no-recommendations">
                  <AlertCircle size={48} />
                  <p>No recommendations available yet.</p>
                  {status === 'pending' || status === 'timeout' ? (
                    <p className="small-text">
                      Generating recommendations... This may take up to 30 seconds.
                      {status === 'timeout' && ' Still processing...'}
                    </p>
                  ) : (
                    <p className="small-text">Try again later or check back soon.</p>
                  )}
                </div>
              )}
            </div>
            
            <div className="recommendations-footer">
              <button 
                className="refresh-button"
                onClick={handleRefreshRecommendations}
              >
                <Clock size={16} />
                Generate New Recommendations
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default RecommendJobButton;