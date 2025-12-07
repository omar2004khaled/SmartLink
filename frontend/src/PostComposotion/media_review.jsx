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
      <label
        style={{
          fontWeight: "600",
          color: "#1e3a8a",
          marginBottom: "1rem",
          display: "block",
        }}
      >
        Selected Media ({mediaFiles.length})
      </label>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill, minmax(180px, 1fr))",
          gap: "1rem",
        }}
      >
        {mediaFiles.map((media) => (
          <div
            key={media.id}
            style={{
              position: "relative",
              borderRadius: "0.75rem",
              overflow: "hidden",
              border: "2px solid #bfdbfe",
            }}
          >
            {media.type === "image" ? (
              <img
                src={media.localUrl}
                alt="Preview"
                style={{
                  width: "100%",
                  height: "10rem",
                  objectFit: "cover",
                }}
              />
            ) : (
              <video
                src={media.localUrl}
                style={{
                  width: "100%",
                  height: "10rem",
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
                  bottom: "0.5rem",
                  left: "0.5rem",
                  background: "rgba(34, 197, 94, 0.9)",
                  color: "white",
                  padding: "0.25rem 0.5rem",
                  borderRadius: "0.25rem",
                  fontSize: "0.75rem",
                  display: "flex",
                  alignItems: "center",
                  gap: "0.25rem",
                }}
              >
                <Check size={12} />
                Uploaded
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
                top: ".5rem",
                right: ".5rem",
                background: "#ef4444",
                color: "white",
                padding: ".3rem",
                borderRadius: "9999px",
                border: "none",
                cursor: "pointer",
              }}
            >
              <X size={14} />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}