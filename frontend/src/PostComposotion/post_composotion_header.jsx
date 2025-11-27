export default function Header(){
    return <>
            <div
        style={{
          background: "linear-gradient(to right, #2563eb, #3b82f6)",
          padding: "2rem clamp(1rem, 4vw, 3rem)",
          flexShrink: 0,
        }}
      >
        <h1 style={{ textAlign: "center", color: "white", fontSize: "2rem", margin: 0 }}>
          Create New Post
        </h1>
        <p style={{ textAlign: "center", color: "#dbeafe", marginTop: ".5rem" }}>
          Share your thoughts with the world
        </p>
      </div>
    </>
}