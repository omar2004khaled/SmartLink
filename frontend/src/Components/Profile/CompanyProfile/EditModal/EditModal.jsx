import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import ImageUploadForm from './ImageUploadForm';
import DescriptionForm from './DescriptionForm';
import OverviewForm from './OverviewForm';
import LocationsForm from './LocationsForm';
import './EditModal.css';

export default function EditModal({ 
  isOpen, 
  section, 
  companyData, 
  onClose, 
  onSave 
}) {
  const [formData, setFormData] = useState({});

  useEffect(() => {
    if (companyData && isOpen) {
      const data = companyData.companyData || companyData;
      setFormData(data);
    }
  }, [companyData, isOpen]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleLocationChange = (index, field, value) => {
    const updatedLocations = [...(formData.locations || [])];
    updatedLocations[index] = {
      ...updatedLocations[index],
      [field]: value
    };
    setFormData(prev => ({
      ...prev,
      locations: updatedLocations
    }));
  };

  const handleAddLocation = () => {
    setFormData(prev => ({
      ...prev,
      locations: [...(prev.locations || []), { city: '', country: '' }]
    }));
  };

  const handleRemoveLocation = (index) => {
    const updatedLocations = (formData.locations || []).filter((_, i) => i !== index);
    setFormData(prev => ({
      ...prev,
      locations: updatedLocations
    }));
  };

  const handleImageUpload = async (e, imageType) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      // Upload to Cloudinary 
      const cloudinaryUrl = 'https://api.cloudinary.com/v1_1/dqhdiihx4/auto/upload';
      const cloudinaryFormData = new FormData();
      cloudinaryFormData.append('file', file);
      cloudinaryFormData.append('upload_preset', 'dyk7gqqw');

      const cloudinaryResponse = await fetch(cloudinaryUrl, {
        method: 'POST',
        body: cloudinaryFormData,
      });

      if (!cloudinaryResponse.ok) {
        throw new Error('Cloudinary upload failed');
      }

      const cloudinaryData = await cloudinaryResponse.json();
      const imageUrl = cloudinaryData.secure_url;

      // Update form data with Cloudinary URL
      setFormData(prev => ({
        ...prev,
        [imageType === 'logo' ? 'logoUrl' : 'coverUrl']: imageUrl
      }));
    } catch (err) {
      console.error('Error uploading image:', err);
    }
  };

  const handleSubmit = () => {
    onSave(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="edit-modal-overlay" onClick={onClose}>
      <div className="edit-modal" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Edit {section?.charAt(0).toUpperCase() + section?.slice(1)}</h2>
          <button className="close-btn" onClick={onClose} title="Close modal">
            <X size={24} />
          </button>
        </div>

        <div className="edit-form">
          {section === 'logo' && (
            <ImageUploadForm
              imageUrl={formData.logoUrl}
              imageType="logo"
              onUpload={handleImageUpload}
            />
          )}

          {section === 'cover' && (
            <ImageUploadForm
              imageUrl={formData.coverUrl}
              imageType="cover"
              onUpload={handleImageUpload}
            />
          )}

          {section === 'description' && (
            <DescriptionForm
              companyName={formData.companyName}
              description={formData.description}
              onChange={handleChange}
            />
          )}

          {section === 'overview' && (
            <OverviewForm
              website={formData.website}
              industry={formData.industry}
              founded={formData.founded}
              onChange={handleChange}
            />
          )}

          {section === 'locations' && (
            <LocationsForm
              locations={formData.locations}
              onLocationChange={handleLocationChange}
              onAddLocation={handleAddLocation}
              onRemoveLocation={handleRemoveLocation}
            />
          )}

          <div className="modal-footer">
            <button type="button" onClick={onClose} className="btn-cancel">
              Cancel
            </button>
            <button 
              type="button" 
              onClick={handleSubmit} 
              className="btn-save" 
            >
              Save
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}