import React from "react";
import "./UserProfile.css";

export default function EducationSection({ education = [], onAdd, onEdit, onDelete }) {
  return (
    <section>
      {education.length === 0 ? (
        <div className="edu-empty" style={{ color: "#6b7280" }}>No education added.</div>
      ) : (
        <div style={{ display: "grid", gap: 12 }}>
          {education.map(edu => (
            <div
              key={edu.id}
              className="edu-card"
              style={{
                display: "flex",
                alignItems: "flex-start",
                justifyContent: "space-between",
                background: "#fbfdff",
                borderRadius: 8,
                padding: 14,
                boxShadow: "0 2px 8px rgba(3,22,39,0.04)",
                border: "1px solid rgba(11,102,195,0.04)"
              }}
            >
              <div style={{ flex: 1 }}>
                <div style={{ display: "flex", gap: 10, alignItems: "baseline", flexWrap: "wrap" }}>
                  <div style={{ fontWeight: 800, color: "#0b1a2b", fontSize: 15 }}>{edu.school}</div>
                  {edu.fieldOfStudy && (
                    <div style={{ color: "#6b7280", fontSize: 13 }}>• {edu.fieldOfStudy}</div>
                  )}
                </div>

                <div style={{ marginTop: 6, color: "#374151", fontSize: 13 }}>
                  {edu.startDate || "—"} {edu.endDate ? `— ${edu.endDate}` : ""}
                </div>

                {edu.description && (
                  <div style={{ marginTop: 8, color: "#475569", fontSize: 14, lineHeight: 1.4 }}>
                    {edu.description}
                  </div>
                )}
              </div>

              <div style={{ marginLeft: 16, display: "flex", gap: 8, alignItems: "center" }}>
                <button
                  className="btn btn-outline btn-sm"
                  onClick={() => onEdit?.(edu.id)}
                >
                  Edit
                </button>

                <button
                  className="btn btn-sm"
                  onClick={() => onDelete?.(edu.id)}
                  style={{
                    background: "linear-gradient(180deg,#ff5a5f,#e04444)",
                    color: "#fff",
                    border: "1px solid rgba(204,36,36,0.12)"
                  }}
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
