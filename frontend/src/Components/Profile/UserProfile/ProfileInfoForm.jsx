import React, { useState, useEffect } from "react";
import "../CompanyProfile/EditModal/EditModal.css";
import { CLOUDINARY_UPLOAD_URL } from "../../../config";
import { useAlert } from "../../../hooks/useAlert";

export default function ProfileInfoForm({ open, profile, locations = [], onSave, onCancel }) {
  const { showError, showSuccess } = useAlert();
  const [form, setForm] = useState({
    userName: "",
    headline: "",
    bio: "",
    gender: "",
    birthDate: "",
    country: "",
    city: "",
    userEmail: "",
    profilePicUrl: ""
  });
  const [useCustomLocation, setUseCustomLocation] = useState(false);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    if (profile) {
      setForm({
        userName: profile.userName || "",
        headline: profile.headline || "",
        bio: profile.bio || "",
        gender: profile.gender || "",
        birthDate: profile.birthDate || "",
        country: profile.country || "",
        city: profile.city || "",
        userEmail: profile.userEmail || "",
        profilePicUrl: profile.profilePicUrl || ""
      });
      setUseCustomLocation(false);
    }
  }, [profile]);

  if (!open) return null;

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  const uploadToCloudinary = async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('upload_preset', 'dyk7gqqw');

      const res = await fetch(CLOUDINARY_UPLOAD_URL, {
        method: 'POST',
        body: formData,
      });

      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }

      const data = await res.json();
      //console.log('Cloudinary response:', data);
      return data.secure_url;
    } catch (err) {
      console.error('Upload failed', err);
      showError(`Upload failed: ${err.message}`);
      return null;
    }
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setUploading(true);
    const uploadedUrl = await uploadToCloudinary(file);
    if (uploadedUrl) {
      setForm({ ...form, profilePicUrl: uploadedUrl });
      showSuccess('Image uploaded successfully!');
    } else {
      showError('Failed to upload image');
    }
    setUploading(false);
  };

  function handleSubmit(e) {
    e.preventDefault();
    //console.log('Submitting form:', form);
    onSave(form);
  }

  return (
    <div className="edit-modal-overlay">
      <div className="edit-modal">
        <div className="modal-header">
          <h2>Edit Profile Info</h2>
          <button type="button" className="close-btn" onClick={onCancel}>
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="edit-form">
            <div className="form-group">
              <label>Headline</label>
              <input name="headline" value={form.headline} onChange={handleChange} className="form-input" />
            </div>

            <div className="form-group">
              <label>Bio</label>
              <textarea name="bio" value={form.bio} onChange={handleChange} className="form-textarea" rows="4" />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Gender</label>
                <input name="gender" value={form.gender} onChange={handleChange} className="form-input" />
              </div>

              <div className="form-group">
                <label>Birth Date</label>
                <input name="birthDate" type="date" value={form.birthDate} onChange={handleChange} className="form-input" />
              </div>
            </div>

            <div className="form-group">
              <label>Location</label>
              {!useCustomLocation ? (
                <select
                  value={form.country + '|' + form.city}
                  onChange={e => {
                    if (e.target.value === '__custom__') {
                      setUseCustomLocation(true);
                    } else {
                      const [country, city] = e.target.value.split('|');
                      setForm({ ...form, country, city });
                    }
                  }}
                  className="form-input"
                >
                  <option value="">Select location</option>
                  {locations.map(loc => (
                    <option key={loc.locationId} value={loc.country + '|' + loc.city}>
                      {loc.country}, {loc.city}
                    </option>
                  ))}
                  <option value="__custom__">Other...</option>
                </select>
              ) : (
                <div className="location-inputs">
                  <input name="country" placeholder="Country" value={form.country} onChange={handleChange} className="form-input" />
                  <input name="city" placeholder="City" value={form.city} onChange={handleChange} className="form-input" />
                  <button type="button" onClick={() => setUseCustomLocation(false)} className="btn-back">Back</button>
                </div>
              )}
            </div>

            <div className="form-group">
              <label>Profile Picture</label>
              <div className="image-upload">
                {form.profilePicUrl && (
                  <img src={form.profilePicUrl} alt="Preview" className="preview" style={{ width: 80, height: 80, borderRadius: '50%', objectFit: 'cover' }} />
                )}
                <label className="upload-btn">
                  <input type="file" accept="image/*" onChange={handleFileChange} disabled={uploading} />
                  {uploading ? 'Uploading...' : 'Choose Photo'}
                </label>
              </div>
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" onClick={onCancel} className="btn-cancel">Cancel</button>
            <button type="submit" className="btn-save" disabled={uploading}>Save</button>
          </div>
        </form>
      </div>
    </div>
  );
}
