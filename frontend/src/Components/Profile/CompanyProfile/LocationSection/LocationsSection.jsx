import React from 'react';
import { MapPin, Edit2 } from 'lucide-react';
import './LocationsSection.css';

export default function LocationsSection({ 
  locations,
  isOwner = false,
  onEdit = null
}) {
  return (
    
    <section className="locations-section">
      <div className="locations-header">
        <h2 className="section-title">Locations ({locations?.length || 0})</h2>
        {isOwner && (
          <button className="edit-btn-small" onClick={onEdit} title="Edit locations">
            <Edit2 size={16} />
          </button>
        )}
      </div>
      <div className="locations-grid">
        {locations && locations.length > 0 ? (
          locations.map((location, index) => (
            <div key={index} className="location-card">
              <MapPin className="location-icon" />
              <div className="location-info">
                <p className="location-address">{location.city}, {location.country}</p>
              </div>
            </div>
          ))
        ) : (
          <p className="no-locations">No locations</p>
        )}
      </div>
    </section>
  );
}
