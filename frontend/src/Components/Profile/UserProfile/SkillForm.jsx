import React, { useState, useEffect } from "react";
import "../CompanyProfile/EditModal/EditModal.css";

export default function SkillForm({ open, skill, onSave, onCancel }) {
  const [form, setForm] = useState({
    skillName: "",
    proficiency: ""
  });

  useEffect(() => {
    if (skill) {
      setForm({
        skillName: skill.skillName || "",
        proficiency: skill.proficiency || ""
      });
    }
  }, [skill]);

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
          <h2>{skill ? "Edit" : "Add"} Skill</h2>
          <button type="button" className="close-btn" onClick={onCancel}>
            ✕
          </button>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="edit-form">
            <div className="form-group">
              <label>Skill Name</label>
              <input name="skillName" value={form.skillName} onChange={handleChange} required className="form-input" />
            </div>
            
            <div className="form-group">
              <label>Proficiency</label>
              <select name="proficiency" value={form.proficiency} onChange={handleChange} required className="form-input">
                <option value="">Select proficiency level</option>
                <option value="Beginner">Beginner</option>
                <option value="Intermediate">Intermediate</option>
                <option value="Advanced">Advanced</option>
                <option value="Expert">Expert</option>
              </select>
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
