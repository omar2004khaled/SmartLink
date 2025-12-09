import React, { useState, useEffect } from "react";

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
    <div style={{ position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh", background: "#0008", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }}>
      <form onSubmit={handleSubmit} style={{ background: "#fff", padding: 32, borderRadius: 12, minWidth: 320, boxShadow: "0 2px 16px #0003" }}>
        <h2>{skill ? "Edit" : "Add"} Skill</h2>
        <label>Skill Name:<br /><input name="skillName" value={form.skillName} onChange={handleChange} required /></label><br />
        <label>Proficiency:<br /><input name="proficiency" value={form.proficiency} onChange={handleChange} /></label><br />
        <div style={{ marginTop: 16 }}>
          <button type="submit">Save</button>
          <button type="button" onClick={onCancel} style={{ marginLeft: 8 }}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
