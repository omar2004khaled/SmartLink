import React from "react";
import "./UserProfile.css";   // ← IMPORTANT

export default function ProfileInfo({ profile, onEdit }) {
  return (
    <section style={{ display: "flex", gap: 24, alignItems: "center", marginBottom: 32 }}>
      <img
        src={profile.profilePicUrl || "https://via.placeholder.com/120"}
        alt="Profile"
        style={{
          width: 120,
          height: 120,
          borderRadius: "50%",
          objectFit: "cover",
          border: "2px solid #eee"
        }}
      />

      <div style={{ flex: 1 }}>
        <h2>{profile.userName}</h2>
        <div style={{ color: "#555" }}>{profile.headline}</div>
        <div style={{ marginTop: 8 }}>{profile.bio}</div>

        <div style={{ marginTop: 8, fontSize: 14, color: "#888" }}>
          Gender: {profile.gender} | Birth: {profile.birthDate}<br />
          Location: {profile.country}, {profile.city}<br />
          Email: {profile.userEmail}
        </div>

        <button onClick={onEdit} className="btn-edit edit-right">Edit Profile</button>

      </div>
    </section>
  );
}
