import React from 'react';

export default function CompanyHeader({ companyName,description ,industry, location, followers}) {
  return (
    <div>
      <h1>{companyName}</h1>
      <p>{description}</p>
      <p>{industry} · {location} · {followers} followers</p>
    </div>
  );
}