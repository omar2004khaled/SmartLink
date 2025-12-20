import React from "react";
import "./UserProfile.css";   // ← IMPORTANT

export default function ProfileInfo({ profile, onEdit, onConnect, connectionStatus, isOwnProfile = true }) {
  return (
    <section style={{ display: "flex", gap: 24, alignItems: "center", marginBottom: 32 }}>
      <img
        src={profile.profilePicUrl || "https://via.placeholder.com/120"}
        alt="Profile"
        onError={(e) => { e.target.src = "https://via.placeholder.com/120"; }}
        style={{
          width: 120,
          height: 120,
          borderRadius: "50%",
          objectFit: "cover",
          border: "2px solid #eee"
        }}
      />

      <div style={{ flex: 1 }}>
        <h1 style={{ fontSize: '32px', fontWeight: '700', margin: '0 0 8px 0', color: '#1a1a1a', fontFamily: '"Bookman Old Style", "Book Antiqua", Palatino, serif', letterSpacing: '-0.5px' }}>{profile.userName}</h1>
        <div style={{ color: "#555" }}>{profile.headline}</div>
        <div style={{ marginTop: 8 }}>{profile.bio}</div>

        <div style={{ marginTop: 8, fontSize: 14, color: "#888" }}>
          Gender: {profile.gender} | Birth: {profile.birthDate}<br />
          Location: {profile.country}, {profile.city}<br />
          Email: {profile.userEmail}
        </div>

        {isOwnProfile ? (
          <button onClick={onEdit} className="btn-edit edit-right">Edit Profile</button>
        ) : onConnect && (
          <div className="edit-right">
            {connectionStatus === 'NONE' && (
              <button onClick={onConnect} className="btn-edit">Connect</button>
            )}
            {connectionStatus === 'PENDING_SENT' && (
              <button onClick={onConnect} className="btn-edit" style={{ background: '#ff5a5f' }}>Cancel</button>
            )}
            {connectionStatus === 'PENDING_RECEIVED' && (
              <div style={{ display: 'flex', gap: '8px' }}>
                <button onClick={onConnect.accept} className="btn-edit" style={{ background: '#0a66c2' }}>Accept</button>
                <button onClick={onConnect.reject} className="btn-edit" style={{ background: '#ff5a5f' }}>Reject</button>
              </div>
            )}
            {connectionStatus === 'ACCEPTED' && (
              <button onClick={onConnect} className="btn-edit" style={{ background: '#ff5a5f' }}>Remove</button>
            )}
          </div>
        )}

      </div>
    </section>
  );
}
