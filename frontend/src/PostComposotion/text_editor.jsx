export default function TextEditor({setPostText,postText}){
    return (
      <textarea
        value={postText}
        onChange={(e) => setPostText(e.target.value)}
        placeholder="What's on your mind?"
        style={{
          width: "100%",
          minHeight: "120px",
          padding: "12px",
          border: "1px solid #d1d5db",
          borderRadius: "6px",
          fontSize: "16px",
          outline: "none",
          resize: "vertical",
          fontFamily: "inherit",
        }}
      />
    )
}