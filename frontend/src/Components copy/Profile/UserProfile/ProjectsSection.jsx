import React from "react";
import "./UserProfile.css";

export default function ProjectsSection({ projects = [], onAdd, onEdit, onDelete }) {
  return (
    <section>
      {projects.length === 0 ? (
        <div className="edu-empty" style={{ color: "#6b7280" }}>No projects added.</div>
      ) : (
        <div style={{ display: "grid", gap: 12 }}>
          {projects.map(proj => (
            <div
              key={proj.id}
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
                <div style={{ fontWeight: 800, color: "#0b1a2b", fontSize: 15 }}>{proj.title}</div>

                <div style={{ marginTop: 6, color: "#374151", fontSize: 13 }}>
                  {proj.startDate || "—"} {proj.endDate ? `— ${proj.endDate}` : ""}
                </div>

                {proj.description && (
                  <div style={{ marginTop: 8, color: "#475569", fontSize: 14, lineHeight: 1.4 }}>
                    {proj.description}
                  </div>
                )}

                {proj.projectUrl && (
                  <div style={{ marginTop: 8 }}>
                    <a href={proj.projectUrl} target="_blank" rel="noopener noreferrer" style={{ color: "#0b66c3", fontWeight: 700 }}>
                      Project Link
                    </a>
                  </div>
                )}
              </div>

              <div style={{ marginLeft: 16, display: "flex", gap: 8, alignItems: "center" }}>
                <button className="btn btn-outline btn-sm" onClick={() => onEdit?.(proj.id)}>Edit</button>
                <button className="btn btn-sm btn-danger" onClick={() => onDelete?.(proj.id)}>Delete</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
