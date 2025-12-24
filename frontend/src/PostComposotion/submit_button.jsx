export default function SubmitButton({handleSubmit,postText,mediaFiles}){
    return <button
          onClick={handleSubmit}
          disabled={!postText.trim() && mediaFiles.length === 0}
          style={{
            width: "100%",
            padding: "12px",
            borderRadius: "6px",
            background: !postText.trim() && mediaFiles.length === 0 ? "#9ca3af" : "#3b82f6",
            color: "white",
            border: "none",
            fontSize: "16px",
            fontWeight: "500",
            cursor: !postText.trim() && mediaFiles.length === 0 ? "not-allowed" : "pointer",
          }}
        >
          Post
    </button>
}
