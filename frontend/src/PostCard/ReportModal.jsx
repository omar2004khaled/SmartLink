import React, { useState } from 'react';
import { Flag, X } from 'lucide-react';
import { reportPost, hasUserReported } from '../FetchData/FetchData';
import './PostCard.css';

const REPORT_CATEGORIES = [
  { value: 'SEXUAL_HARASSMENT', label: 'Sexual Harassment' },
  { value: 'HATE_SPEECH', label: 'Hate Speech' },
  { value: 'SPAM_OR_SCAM', label: 'Spam or Scam' },
  { value: 'FALSE_INFORMATION', label: 'False Information' },
  { value: 'BULLYING_OR_HARASSMENT', label: 'Bullying or Harassment' },
  { value: 'INAPPROPRIATE_CONTENT', label: 'Inappropriate Content' },
  { value: 'VIOLENCE_OR_THREATS', label: 'Violence or Threats' },
  { value: 'INTELLECTUAL_PROPERTY_VIOLATION', label: 'Intellectual Property Violation' },
  { value: 'OTHER', label: 'Other' }
];

const ReportModal = ({ postId, onClose, onReportSuccess }) => {
  const [selectedCategory, setSelectedCategory] = useState('');
  const [description, setDescription] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedCategory) {
      setError('Please select a report category');
      return;
    }

    setIsSubmitting(true);
    setError('');

    try {
      const reporterId = localStorage.getItem('userId');
      if (!reporterId) {
        setError('You must be logged in to report posts');
        return;
      }

      // Check if user already reported
      const alreadyReported = await hasUserReported(postId, reporterId);
      if (alreadyReported) {
        setError('You have already reported this post');
        return;
      }

      await reportPost(postId, reporterId, selectedCategory, description);
      setSuccess(true);
      setTimeout(() => {
        onReportSuccess && onReportSuccess();
        onClose();
      }, 2000);
    } catch (err) {
      console.error('Report submission error:', err);
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.message) {
        setError(err.message);
      } else {
        setError('Failed to submit report. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  if (success) {
    return (
      <div className="report-modal-overlay" onClick={onClose}>
        <div className="report-modal" onClick={(e) => e.stopPropagation()}>
          <div className="report-modal-header">
            <h3>Report Submitted</h3>
            <button className="close-button" onClick={onClose}>
              <X size={20} />
            </button>
          </div>
          <div className="report-modal-body">
            <div className="success-message">
              <Flag size={48} color="#10b981" />
              <p>Thank you for your report. We will review it shortly.</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="report-modal-overlay" onClick={onClose}>
      <div className="report-modal" onClick={(e) => e.stopPropagation()}>
        <div className="report-modal-header">
          <h3>Report Post</h3>
          <button className="close-button" onClick={onClose}>
            <X size={20} />
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="report-modal-body">
            <div className="form-group">
              <label htmlFor="category">Report Category *</label>
              <select
                id="category"
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
                required
              >
                <option value="">Select a category</option>
                {REPORT_CATEGORIES.map(category => (
                  <option key={category.value} value={category.value}>
                    {category.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="description">Additional Details (Optional)</label>
              <textarea
                id="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Provide any additional context..."
                maxLength={500}
                rows={4}
              />
              <small>{description.length}/500 characters</small>
            </div>

            {error && <div className="error-message">{error}</div>}
          </div>

          <div className="report-modal-footer">
            <button type="button" onClick={onClose} className="cancel-button">
              Cancel
            </button>
            <button type="submit" disabled={isSubmitting} className="submit-button">
              {isSubmitting ? 'Submitting...' : 'Submit Report'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ReportModal;