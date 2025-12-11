import React from "react";
import "./UserProfile.css";

export default function ExperienceSection({ experience = [], onAdd, onEdit, onDelete }) {
  return (
    <section>
      {experience.length === 0 ? (
        <div className="edu-empty" style={{ color: "#6b7280" }}>No experience added.</div>
      ) : (
        <div style={{ display: "grid", gap: 12 }}>
          {experience.map(exp => (
            <div
              key={exp.id}
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
                  <div style={{ fontWeight: 800, color: "#0b1a2b", fontSize: 15 }}>{exp.title}</div>
                  {exp.companyName && <div style={{ color: "#6b7280", fontSize: 13 }}>• {exp.companyName}</div>}
                  {exp.location && <div style={{ color: "#6b7280", fontSize: 13 }}>• {exp.location}</div>}
                </div>

                <div style={{ marginTop: 6, color: "#374151", fontSize: 13 }}>
                  {exp.startDate || "—"} {exp.endDate ? `— ${exp.endDate}` : ""}
                </div>

                {exp.description && (
                  <div style={{ marginTop: 8, color: "#475569", fontSize: 14, lineHeight: 1.4 }}>
                    {exp.description}
                  </div>
                )}
              </div>

              {(onEdit || onDelete) && (
                <div style={{ marginLeft: 16, display: "flex", gap: 8, alignItems: "center" }}>
                  {onEdit && (
                    <button
                      className="btn btn-outline btn-sm"
                      onClick={() => onEdit(exp.id)}
                    >
                      Edit
                    </button>
                  )}

                  {onDelete && (
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => onDelete(exp.id)}
                    >
                      Delete
                    </button>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
