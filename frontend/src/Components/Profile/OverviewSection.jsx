import React from 'react';

export default function OverviewSection({ description, website, industry,founded }) {
  return (
    <section>
      <h2>Overview</h2>
      <p>{description}</p>

      <div>
        <div>
          <h3>Website</h3>
          <a href={website} target="_blank" rel="noopener noreferrer">
            {website}
          </a>
        </div>

        <div>
          <h3>Industry</h3>
          <p>{industry}</p>
        </div>

        <div>
          <h3>Foundation Year</h3>
          <p>{founded}</p>
        </div>

      </div>
    </section>
  );
}