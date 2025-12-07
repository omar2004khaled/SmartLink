import React from 'react';
import { Camera } from 'lucide-react';
import './CompanyLogo.css';

export default function CompanyLogo({ 
  logoUrl, 
  coverUrl, 
  companyName,
  isOwner = false,
  onEditCover = null,
  onEditLogo = null
}) {
  return (
    <div className="company-logo-wrapper">
      <div className="cover-image-container">
        <img src={coverUrl} alt={companyName} className="cover-image" />
        {isOwner && (
          
            <button className="edit-cover-btn" onClick={onEditCover} title="Edit cover">
              <Camera size={20}  />
            </button>

        )}
      </div>
      <div className="logo-image-container">
        <img src={logoUrl} alt={companyName} className="logo-image" />
        {isOwner && (
          <button className="edit-logo-btn" onClick={onEditLogo} title="Edit logo">
            <Camera size={16} />
          </button>
        )}
      </div>
    </div>
  );
}