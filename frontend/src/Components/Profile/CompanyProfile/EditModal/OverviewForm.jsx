import React from 'react';
import './OverviewForm.css';

export default function OverviewForm({ 
  website, 
  industry, 
  founded, 
  onChange 
}) {
  return (
    <>
      <div className="form-group">
        <label>Website</label>
        <input
          type="url"
          name="website"
          value={website || ''}
          onChange={onChange}
          placeholder="https://example.com"
          className="form-input"
        />
      </div>

      <div className="form-group">
        <label>Industry</label>
        <input
          type="text"
          required
          name="industry"
          value={industry}
          onChange={onChange}
          className="form-input"
        />
      </div>

      <div className="form-group">
        <label>Founded Year</label>
        <input
          type="number"
          name="founded"
          value={founded || ''}
          onChange={onChange}
          min="1900"
          max={new Date().getFullYear()}
          className="form-input"
        />
      </div>
    </>
  );
}
