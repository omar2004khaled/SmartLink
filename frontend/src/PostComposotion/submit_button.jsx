export default function SubmitButton({handleSubmit,postText,mediaFiles}){
    return <button
          onClick={handleSubmit}
          disabled={!postText.trim() && mediaFiles.length === 0}
          style={{
            padding: "1.3rem",
            borderRadius: "0.75rem",
            background:
              !postText.trim() && mediaFiles.length === 0
                ? "#9ca3af"
                : "linear-gradient(to right, #2563eb, #3b82f6)",
            color: "white",
            border: "none",
            fontWeight: "600",
            fontSize: "1.1rem",
            cursor:
              !postText.trim() && mediaFiles.length === 0
                ? "not-allowed"
                : "pointer",
          }}
        >
          Publish Post
    </button>
}