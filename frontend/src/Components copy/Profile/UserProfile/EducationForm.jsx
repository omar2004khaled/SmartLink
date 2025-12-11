import React, { useState, useEffect } from "react";

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
    <div style={{ position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh", background: "#0008", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }}>
      <form onSubmit={handleSubmit} style={{ background: "#fff", padding: 32, borderRadius: 12, minWidth: 320, boxShadow: "0 2px 16px #0003" }}>
        <h2>{education ? "Edit" : "Add"} Education</h2>
        <label>School:<br /><input name="school" value={form.school} onChange={handleChange} required /></label><br />
        <label>Field of Study:<br /><input name="fieldOfStudy" value={form.fieldOfStudy} onChange={handleChange} /></label><br />
        <label>Start Date:<br /><input name="startDate" type="date" value={form.startDate} onChange={handleChange} /></label><br />
        <label>End Date:<br /><input name="endDate" type="date" value={form.endDate} onChange={handleChange} /></label><br />
        <label>Description:<br /><textarea name="description" value={form.description} onChange={handleChange} /></label><br />
        <div style={{ marginTop: 16 }}>
          <button type="submit">Save</button>
          <button type="button" onClick={onCancel} style={{ marginLeft: 8 }}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
