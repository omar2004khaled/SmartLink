import React from 'react';
import { Plus } from 'lucide-react';
import LocationItem from './LocationItem';
import './LocationsForm.css';

export default function LocationsForm({ 
  locations, 
  onLocationChange, 
  onAddLocation, 
  onRemoveLocation 
}) {
  return (
    <div className="form-group">
      <div className="locations-header">
        <label>Locations</label>
        <button 
          type="button" 
          className="btn-add-location"
          onClick={onAddLocation}
        >
          <Plus size={18} />
          Add Location
        </button>
      </div>
      
      <div className="locations-list">
        {locations && locations.length > 0 ? (
          locations.map((location, index) => (
            <LocationItem
              key={index}
              location={location}
              index={index}
              onChange={onLocationChange}
              onRemove={onRemoveLocation}
            />
          ))
        ) : (
          <p className="no-locations">No locations added yet</p>
        )}
      </div>
    </div>
  );
}