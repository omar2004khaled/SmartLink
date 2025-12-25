import { X, Check } from "lucide-react"

export default function MediaReviewer({mediaFiles, setMediaFiles}) {
  const removeMedia = (id) => {
    setMediaFiles((prev) => {
      const updated = prev.filter((media) => media.id !== id);
      const removed = prev.find((media) => media.id === id);

      // Revoke the local preview URL to free memory
      if (removed && removed.localUrl) {
        URL.revokeObjectURL(removed.localUrl);
      }
      return updated;
    });
  };

  return (
    <div>
      <div style={{ fontSize: "14px", color: "#6b7280", marginBottom: "8px" }}>
        Media ({mediaFiles.length})
      </div>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill, minmax(120px, 1fr))",
          gap: "12px",
        }}
      >
        {mediaFiles.map((media) => (
          <div
            key={media.id}
            style={{
              position: "relative",
              borderRadius: "6px",
              overflow: "hidden",
              border: "1px solid #d1d5db",
            }}
          >
            {media.type === "image" ? (
              <img
                src={media.localUrl}
                alt="Preview"
                style={{
                  width: "100%",
                  height: "100px",
                  objectFit: "cover",
                }}
              />
            ) : (
              <video
                src={media.localUrl}
                style={{
                  width: "100%",
                  height: "100px",
                  objectFit: "cover",
                }}
                controls
              />
            )}

            {/* Upload status indicator */}
            {!media.uploadedUrl ? (
              <div
                style={{
                  position: "absolute",
                  bottom: "0.5rem",
                  left: "0.5rem",
                  background: "rgba(0, 0, 0, 0.7)",
                  color: "white",
                  padding: "0.25rem 0.5rem",
                  borderRadius: "0.25rem",
                  fontSize: "0.75rem",
                  display: "flex",
                  alignItems: "center",
                  gap: "0.25rem",
                }}
              >
              
               
              </div>
            ) : (
              <div
                style={{
                  position: "absolute",
                  bottom: "4px",
                  left: "4px",
                  background: "rgba(34, 197, 94, 0.9)",
                  color: "white",
                  padding: "2px 6px",
                  borderRadius: "4px",
                  fontSize: "10px",
                }}
              >
                ✓
              </div>
            )}

            <style>
              {`
                @keyframes spin {
                  to { transform: rotate(360deg); }
                }
              `}
            </style>

            <button
              onClick={() => removeMedia(media.id)}
              style={{
                position: "absolute",
                top: "4px",
                right: "4px",
                background: "#ef4444",
                color: "white",
                padding: "4px",
                borderRadius: "50%",
                border: "none",
                cursor: "pointer",
                width: "24px",
                height: "24px",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <X size={12} />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
