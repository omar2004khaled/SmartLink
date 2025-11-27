import React from 'react';
import { Edit2 } from 'lucide-react';
import './CompanyHeader.css';

export default function CompanyHeader({ 
  companyName,
  description,
  industry, 
  followers,
  isOwner = false,
  onEditDescription = null
}) {
  return (
    <div className="company-header">
      <h1 className="company-name">{companyName}</h1>
      <div className="description-wrapper">
        <p className="company-description">{description}</p>
        {isOwner && onEditDescription && (
          <button className="edit-btn-small" onClick={onEditDescription} title="Edit description">
            <Edit2 size={16} />
          </button>
        )}
      </div>
      <p className="company-meta">
        {industry} ·{followers} followers
      </p>
    </div>
  );
}