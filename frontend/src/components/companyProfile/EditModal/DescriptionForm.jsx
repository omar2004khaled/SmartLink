import React from 'react';
import './DescriptionForm.css';

export default function DescriptionForm({ 
  companyName, 
  description, 
  onChange 
}) {
  return (
    <>
      <div className="form-group">
        <label>Company Name</label>
        <input
          type="text"
          name="companyName"
          value={companyName || ''}
          onChange={onChange}
          maxLength="100"
          placeholder="Enter company name"
          className="form-input"
        />
        <p className="char-count">{(companyName || '').length}/100</p>
      </div>
      
      <div className="form-group">
        <label>Company Description</label>
        <textarea
          name="description"
          value={description || ''}
          onChange={onChange}
          rows="5"
          maxLength="500"
          placeholder="Enter company description"
          className="form-textarea"
        />
        <p className="char-count">{(description || '').length}/500</p>
      </div>
    </>
  );
}