import React from 'react';
import './OverviewSection.css';

export default function OverviewSection({ description, website, industry, founded }) {
  return (
    <section className="overview-section">
      <h2 className="section-title">Overview</h2>
      <p className="section-description">{description}</p>

      <div className="info-grid">
        <div className="info-item">
          <h3 className="info-label">Website</h3>
          <a href={website} target="_blank" rel="noopener noreferrer" className="info-link">
            {website}
          </a>
        </div>

        <div className="info-item">
          <h3 className="info-label">Industry</h3>
          <p className="info-value">{industry}</p>
        </div>

        <div className="info-item">
          <h3 className="info-label">Foundation Year</h3>
          <p className="info-value">{founded}</p>
        </div>

      </div>
    </section>
  );
}