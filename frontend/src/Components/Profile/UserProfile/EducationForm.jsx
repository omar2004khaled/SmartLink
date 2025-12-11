import React, { useState, useEffect } from "react";
import "../CompanyProfile/EditModal/EditModal.css";

export default function EducationForm({ open, education, onSave, onCancel }) {
  const [form, setForm] = useState({
    school: "",
    fieldOfStudy: "",
    startDate: "",
    endDate: "",
    description: ""
  });

  useEffect(() => {
    if (education) {
      setForm({
        school: education.school || "",
        fieldOfStudy: education.fieldOfStudy || "",
        startDate: education.startDate || "",
        endDate: education.endDate || "",
        description: education.description || ""
      });
    }
  }, [education]);

  if (!open) return null;

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function handleSubmit(e) {
    e.preventDefault();
    onSave(form);
  }

  return (
    <div className="edit-modal-overlay">
      <div className="edit-modal">
        <div className="modal-header">
          <h2>{education ? "Edit" : "Add"} Education</h2>
          <button type="button" className="close-btn" onClick={onCancel}>
            ✕
          </button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="edit-form">
            <div className="form-group">
              <label>School</label>
              <input name="school" value={form.school} onChange={handleChange} required className="form-input" />
            </div>
            
            <div className="form-group">
              <label>Field of Study</label>
              <input name="fieldOfStudy" value={form.fieldOfStudy} onChange={handleChange} className="form-input" />
            </div>
            
            <div className="form-row">
              <div className="form-group">
                <label>Start Date</label>
                <input name="startDate" type="date" value={form.startDate} onChange={handleChange} className="form-input" />
              </div>
              
              <div className="form-group">
                <label>End Date</label>
                <input name="endDate" type="date" value={form.endDate} onChange={handleChange} className="form-input" />
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
