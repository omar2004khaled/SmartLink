import React from 'react';
import { MapPin, ExternalLink } from 'lucide-react';
import './LocationsSection.css';

export default function LocationsSection({ locations }) {
  return (
    <section className="locations-section">
      <h2 className="section-title">Locations ({locations.length})</h2>
      <div className="locations-grid">
        {locations.map((location, index) => (
          <div key={index} className="location-card">
            <MapPin className="location-icon" />
            <div className="location-info">
              <p className="location-address">{location.city} , {location.country}</p>

            </div>
          </div>
        ))}
      </div>
    </section>
  );
}