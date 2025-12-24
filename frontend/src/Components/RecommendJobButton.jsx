import React, { useState, useEffect } from 'react';
import { Sparkles, Loader2, Clock, AlertCircle } from 'lucide-react';
import { RecommendationService } from './RecommendationService';
import './RecommendJobButton.css';

const RecommendJobButton = ({ userId }) => {
  const [showRecommendations, setShowRecommendations] = useState(false);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState('not_started');
  const [polling, setPolling] = useState(false);

  useEffect(() => {
    // Check initial status
    const storedStatus = RecommendationService.checkRecommendationStatus(userId);
    setStatus(storedStatus.status);
    if (storedStatus.data) {
      setRecommendations(storedStatus.data);
    }
  }, [userId]);

  const handleButtonClick = async () => {
    if (status === 'ready' && recommendations.length > 0) {
      setShowRecommendations(true);
      return;
    }

    setLoading(true);
    
    if (status === 'not_started' || status === 'failed' || status === 'timeout') {
      // Start generation
      RecommendationService.setRecommendationStatus(userId, 'generating');
      setStatus('generating');
      
      const success = await RecommendationService.triggerRecommendationGeneration(userId);
      if (success) {
        RecommendationService.setRecommendationStatus(userId, 'pending');
        setStatus('pending');
        startPolling();
      } else {
        RecommendationService.setRecommendationStatus(userId, 'failed');
        setStatus('failed');
        setLoading(false);
      }
    } else if (status === 'pending') {
      // Already generating, just poll
      startPolling();
    }
  };

  const startPolling = () => {
    setPolling(true);
    pollForRecommendations();
  };

  const pollForRecommendations = async () => {
    let attempts = 0;
    const maxAttempts = 60; // 5 minutes max
    
    const poll = async () => {
      attempts++;
      
      try {
        const data = await RecommendationService.getJobSeekerRecommendations(userId);
        
        if (data && data.length > 0) {
          // Recommendations ready
          RecommendationService.setRecommendationStatus(userId, 'ready', data);
          setStatus('ready');
          setRecommendations(data);
          setPolling(false);
          setLoading(false);
          setShowRecommendations(true);
          return true;
        }
        
        if (attempts >= maxAttempts) {
          // Timeout
          RecommendationService.setRecommendationStatus(userId, 'timeout');
          setStatus('timeout');
          setPolling(false);
          setLoading(false);
          return false;
        }
        
        // Continue polling
        setTimeout(poll, 5000);
      } catch (error) {
        console.error('Polling error:', error);
        if (attempts >= maxAttempts) {
          RecommendationService.setRecommendationStatus(userId, 'error');
          setStatus('error');
          setPolling(false);
          setLoading(false);
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
      case 'generating':
        return 'Starting Recommendations...';
      case 'pending':
        return 'Generating Recommendations...';
      case 'failed':
        return 'Try Generating Recommendations';
      case 'timeout':
        return 'Generate Recommendations (Timeout)';
      default:
        return 'Recommend a Job';
    }
  };

  return (
    <>
      <button
        onClick={handleButtonClick}
        disabled={loading || polling}
        className="recommend-job-button"
      >
        {loading || polling ? (
          <>
            <Loader2 className="animate-spin" size={16} />
            <span>{polling ? 'Generating...' : 'Loading...'}</span>
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
                        <h4>{job.title}</h4>
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
                      
                      <div className="job-actions">
                        <button className="apply-button">View Details</button>
                        <button className="save-button">Save</button>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="no-recommendations">
                  <AlertCircle size={48} />
                  <p>No recommendations available yet.</p>
                  <p className="small-text">Try again later or check back soon.</p>
                </div>
              )}
            </div>
            
            <div className="recommendations-footer">
              <button 
                className="refresh-button"
                onClick={() => {
                  RecommendationService.setRecommendationStatus(userId, 'not_started');
                  setStatus('not_started');
                  setShowRecommendations(false);
                  handleButtonClick();
                }}
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