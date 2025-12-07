export default function TextEditor({setPostText,postText}){
    return <div>
          <label
            style={{
              display: "block",
              marginBottom: ".6rem",
              fontWeight: "600",
              color: "#1e3a8a",
            }}
          >
            What's on your mind?
          </label>

          <textarea
            value={postText}
            onChange={(e) => setPostText(e.target.value)}
            placeholder="Write something..."
            style={{
              width: "100%",
              minHeight: "200px",
              padding: "1.2rem",
              border: "2px solid #bfdbfe",
              borderRadius: "0.75rem",
              fontSize: "1.1rem",
              outline: "none",
              resize: "none",
            }}
          />
    </div>
}