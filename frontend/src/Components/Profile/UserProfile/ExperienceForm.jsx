import React, { useState, useEffect } from "react";
import "../CompanyProfile/EditModal/EditModal.css";

export default function ExperienceForm({ open, experience, onSave, onCancel }) {
  const [form, setForm] = useState({
    companyName: "",
    title: "",
    location: "",
    startDate: "",
    endDate: "",
    description: ""
  });

  useEffect(() => {
    if (experience) {
      setForm({
        companyName: experience.companyName || "",
        title: experience.title || "",
        location: experience.location || "",
        startDate: experience.startDate || "",
        endDate: experience.endDate || "",
        description: experience.description || ""
      });
    }
  }, [experience]);

  if (!open) return null;

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function handleSubmit(e) {
    e.preventDefault();
    if (form.startDate && form.endDate && form.startDate >= form.endDate) {
      alert('Start date must be before end date');
      return;
    }
    onSave(form);
  }

  return (
    <div className="edit-modal-overlay">
      <div className="edit-modal">
        <div className="modal-header">
          <h2>{experience ? "Edit" : "Add"} Experience</h2>
          <button type="button" className="close-btn" onClick={onCancel}>
            ✕
          </button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="edit-form">
            <div className="form-group">
              <label>Company Name</label>
              <input name="companyName" value={form.companyName} onChange={handleChange} required className="form-input" />
            </div>
            
            <div className="form-group">
              <label>Title</label>
              <input name="title" value={form.title} onChange={handleChange} required className="form-input" />
            </div>
            
            <div className="form-group">
              <label>Location</label>
              <input name="location" value={form.location} onChange={handleChange} required className="form-input" />
            </div>
            
            <div className="form-row">
              <div className="form-group">
                <label>Start Date</label>
                <input name="startDate" type="date" value={form.startDate} onChange={handleChange} required className="form-input" max={new Date().toISOString().split('T')[0]} />
              </div>
              
              <div className="form-group">
                <label>End Date</label>
                <input name="endDate" type="date" value={form.endDate} onChange={handleChange} required className="form-input" />
              </div>
            </div>
            
            <div className="form-group">
              <label>Description</label>
              <textarea name="description" value={form.description} onChange={handleChange} className="form-textarea" rows="4" />
            </div>
          </div>
          
          <div className="modal-footer">
            <button type="button" onClick={onCancel} className="btn-cancel">Cancel</button>
            <button type="submit" className="btn-save">Save</button>
          </div>
        </form>
      </div>
    </div>
  );
}
