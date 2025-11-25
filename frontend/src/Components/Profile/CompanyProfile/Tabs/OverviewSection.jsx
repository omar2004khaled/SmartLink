import React from 'react';
import { Edit2 } from 'lucide-react';
import './OverviewSection.css';

export default function OverviewSection({ 
  description, 
  website, 
  industry, 
  founded,
  isOwner = false,
  onEdit = null 
}) {
  return (
    <section className="overview-section">
      <div className="overview-header">
        <h2 className="section-title">About</h2>
        {isOwner && onEdit && (
          <button className="edit-btn-small" onClick={onEdit} title="Edit overview">
            <Edit2 size={16} />
          </button>
        )}
      </div>
      <p className="section-description">{description}</p>

      <div className="info-grid">
        {website && (
          <div className="info-item">
            <h3 className="info-label">Website</h3>
            <a href={website} target="_blank" rel="noopener noreferrer" className="info-link">
              {website}
            </a>
          </div>
        )}

        {industry && (
          <div className="info-item">
            <h3 className="info-label">Industry</h3>
            <p className="info-value">{industry}</p>
          </div>
        )}

        {founded && (
          <div className="info-item">
            <h3 className="info-label">Foundation Year</h3>
            <p className="info-value">{founded}</p>
          </div>
        )}
      </div>
    </section>
  );
}