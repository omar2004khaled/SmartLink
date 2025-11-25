import React from 'react';
import './CompanyLogo.css'

export default function CompanyLogo({ logoUrl,coverUrl, companyName }) {
  return (
    <div className="company-logo-wrapper">
      <img src={coverUrl} alt={companyName} className="cover-image" />
      <img src={logoUrl} alt={companyName} className="logo-image" />
    </div>
  );
}