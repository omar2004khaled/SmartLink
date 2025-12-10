import React from "react";
import "./UserProfile.css";

export default function SkillsSection({ skills = [], onAdd, onEdit, onDelete }) {
  return (
    <section>
      {skills.length === 0 ? (
        <div className="edu-empty" style={{ color: "#6b7280" }}>No skills added.</div>
      ) : (
        <div style={{ display: "grid", gap: 12 }}>
          {skills.map(skill => (
            <div
              key={skill.id}
              className="edu-card"
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                background: "#fbfdff",
                borderRadius: 8,
                padding: 12,
                boxShadow: "0 2px 8px rgba(3,22,39,0.04)",
                border: "1px solid rgba(11,102,195,0.04)"
              }}
            >
              <div style={{ display: "flex", flexDirection: "column", gap: 4 }}>
                <div style={{ fontWeight: 700, color: "#0b1a2b", fontSize: 15 }}>{skill.skillName}</div>
                {skill.proficiency && <div style={{ color: "#6b7280", fontSize: 13 }}>{skill.proficiency}</div>}
              </div>

              {(onEdit || onDelete) && (
                <div style={{ display: "flex", gap: 8 }}>
                  {onEdit && <button className="btn btn-outline btn-sm" onClick={() => onEdit(skill.id)}>Edit</button>}
                  {onDelete && <button className="btn btn-sm btn-danger" onClick={() => onDelete(skill.id)}>Delete</button>}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
