import React, { useState, useEffect } from "react";

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
    <div style={{ position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh", background: "#0008", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }}>
      <form onSubmit={handleSubmit} style={{ background: "#fff", padding: 32, borderRadius: 12, minWidth: 320, boxShadow: "0 2px 16px #0003" }}>
        <h2>{project ? "Edit" : "Add"} Project</h2>
        <label>Title:<br /><input name="title" value={form.title} onChange={handleChange} required /></label><br />
        <label>Description:<br /><textarea name="description" value={form.description} onChange={handleChange} /></label><br />
        <label>Project URL:<br /><input name="projectUrl" value={form.projectUrl} onChange={handleChange} /></label><br />
        <label>Start Date:<br /><input name="startDate" type="date" value={form.startDate} onChange={handleChange} /></label><br />
        <label>End Date:<br /><input name="endDate" type="date" value={form.endDate} onChange={handleChange} /></label><br />
        <div style={{ marginTop: 16 }}>
          <button type="submit">Save</button>
          <button type="button" onClick={onCancel} style={{ marginLeft: 8 }}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
