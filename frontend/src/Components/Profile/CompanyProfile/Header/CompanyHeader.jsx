import React from 'react';
import './CompanyHeader.css';

export default function CompanyHeader({ companyName,description ,industry, location, followers}) {
  return (
    <div className="company-header">
      <h1 className="company-name">{companyName}</h1>
      <p className="company-description">{description}</p>
      <p className="company-meta">{industry} · {location} · {followers} followers</p>
    </div>
  );
}