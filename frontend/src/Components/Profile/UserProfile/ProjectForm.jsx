import React, { useState, useEffect } from "react";
import "../CompanyProfile/EditModal/EditModal.css";

export default function ProjectForm({ open, project, onSave, onCancel }) {
  const [form, setForm] = useState({
    title: "",
    description: "",
    projectUrl: "",
    startDate: "",
    endDate: ""
  });

  useEffect(() => {
    if (project) {
      setForm({
        title: project.title || "",
        description: project.description || "",
        projectUrl: project.projectUrl || "",
        startDate: project.startDate || "",
        endDate: project.endDate || ""
      });
    }
  }, [project]);

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
          <h2>{project ? "Edit" : "Add"} Project</h2>
          <button type="button" className="close-btn" onClick={onCancel}>
            ✕
          </button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="edit-form">
            <div className="form-group">
              <label>Title</label>
              <input name="title" value={form.title} onChange={handleChange} required className="form-input" />
            </div>
            
            <div className="form-group">
              <label>Description</label>
              <textarea name="description" value={form.description} onChange={handleChange} className="form-textarea" rows="4" />
            </div>
            
            <div className="form-group">
              <label>Project URL</label>
              <input name="projectUrl" type="url" value={form.projectUrl} onChange={handleChange} className="form-input" placeholder="https://example.com" />
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
