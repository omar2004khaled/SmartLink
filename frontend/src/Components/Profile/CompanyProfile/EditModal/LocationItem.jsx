import React from 'react';
import { Trash2 } from 'lucide-react';
import './LocationItem.css';

export default function LocationItem({ 
  location, 
  index, 
  onChange, 
  onRemove 
}) {
  return (
    <div className="location-item">
      <div className="location-inputs">
        <div className="location-field">
          <label>City</label>
          <input 
            type="text"
            name="city"
            value={location.city || ''}
            onChange={(e) => onChange(index, 'city', e.target.value)}
            placeholder="Enter city"
            className="form-input"
          />
        </div>

        <div className="location-field" id="country">
          <label>Country</label>
          <input 
            type="text"
            name="country"
            value={location.country || ''}
            onChange={(e) => onChange(index, 'country', e.target.value)}
            placeholder="Enter country"
            className="form-input"
          />
        </div>
      </div>

      <button
        type="button"
        className="btn-remove-location"
        onClick={() => onRemove(index)}
        title="Remove location"
      >
        <Trash2 size={18} />
      </button>
    </div>
  );
}
