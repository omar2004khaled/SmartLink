import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import ImageUploadForm from './ImageUploadForm';
import DescriptionForm from './DescriptionForm';
import OverviewForm from './OverviewForm';
import LocationsForm from './LocationsForm';
import './EditModal.css';
import { CLOUDINARY_UPLOAD_URL } from '../../../../config';

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
      // Fix: Handle both nested and direct companyData structure
      const data = companyData.companyData || companyData;
      setFormData({
        companyName: data.companyName || '',
        description: data.description || '',
        website: data.website || '',
        industry: data.industry || '',
        founded: data.founded || null,
        logoUrl: data.logoUrl || '',
        coverUrl: data.coverUrl || '',
        coverImageUrl: data.coverImageUrl || '',
        locations: data.locations || []
      });
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
      const cloudinaryFormData = new FormData();
      cloudinaryFormData.append('file', file);
      cloudinaryFormData.append('upload_preset', 'dyk7gqqw');

      const cloudinaryResponse = await fetch(CLOUDINARY_UPLOAD_URL, {
        method: 'POST',
        body: cloudinaryFormData,
      });

      if (!cloudinaryResponse.ok) {
        throw new Error('Cloudinary upload failed');
      }

      const cloudinaryData = await cloudinaryResponse.json();
      const imageUrl = cloudinaryData.secure_url;
      
      console.log('Image uploaded:', imageType, imageUrl);

      // FIX 1: Update BOTH logoUrl/coverUrl AND coverImageUrl for backend compatibility
      if (imageType === 'logo') {
        setFormData(prev => ({
          ...prev,
          logoUrl: imageUrl
        }));
      } else if (imageType === 'cover') {
        setFormData(prev => ({
          ...prev,
          coverUrl: imageUrl,
          coverImageUrl: imageUrl  // Backend uses this field name
        }));
      }
    } catch (err) {
      console.error('Error uploading image:', err);
      alert('Failed to upload image. Please try again.');
    }
  };

  const handleSubmit = () => {
    // Prepare data based on which section is being edited
    let dataToSave = {};

    if (section === 'logo') {
      dataToSave = {
        logoUrl: formData.logoUrl
      };
    } else if (section === 'cover') {
      dataToSave = {
        coverImageUrl: formData.coverImageUrl || formData.coverUrl
      };
    } else if (section === 'description') {
      dataToSave = {
        companyName: formData.companyName,
        description: formData.description
      };
    } else if (section === 'overview') {
      dataToSave = {
        website: formData.website,
        industry: formData.industry,
        founded: formData.founded ? parseInt(formData.founded) : null
      };
    } else if (section === 'locations') {
      // FIX: Send ALL current locations (including empty array if all deleted)
      // Filter out empty/invalid locations
      const validLocations = (formData.locations || [])
        .filter(loc => loc.city && loc.country)
        .filter(loc => loc.city.trim() !== '' && loc.country.trim() !== '')
        .map(loc => ({
          locationId: loc.locationId || null,
          city: loc.city.trim(),
          country: loc.country.trim()
        }));
      
      dataToSave = {
        locations: validLocations
      };
    }

    console.log('Saving data for section:', section, dataToSave);
    onSave(dataToSave);
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
              imageUrl={formData.coverUrl || formData.coverImageUrl}
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