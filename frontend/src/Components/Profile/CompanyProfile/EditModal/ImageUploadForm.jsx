import React from 'react';
import { Upload } from 'lucide-react';
import './ImageUploadForm.css';
import { PROFILE_PLACEHOLDER, COVER_PLACEHOLDER } from '../../../../assets/placeholders';

export default function ImageUploadForm({ 
  imageUrl, 
  imageType, 
  onUpload, 
}) {
  return (
    <div className="form-group">
      <label>{imageType === 'logo' ? 'Company Logo' : 'Cover Image'}</label>
      <div className="image-upload">
        <img src={imageUrl || (imageType === 'logo' ? PROFILE_PLACEHOLDER : COVER_PLACEHOLDER)} alt={imageType} className="preview" />
        <label className="upload-btn">
          <Upload size={20} />
          Choose Image
          <input 
            type="file" 
            accept="image/*" 
            onChange={(e) => onUpload(e, imageType)}
            style={{ display: 'none' }}
          />
        </label>
      </div>
    </div>
  );
}