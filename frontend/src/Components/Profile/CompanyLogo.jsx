import React from 'react';

export default function CompanyLogo({ logoUrl,coverUrl, companyName }) {
  return (
    <div>
        <img src={coverUrl} alt={companyName}/>
      <img src={logoUrl} alt={companyName} />
    </div>
  );
}