import React, { useState, useEffect } from "react";

export default function ProfileInfoForm({ open, profile, locations = [], onSave, onCancel }) {
  const [form, setForm] = useState({
    userName: "",
    headline: "",
    bio: "",
    gender: "",
    birthDate: "",
    country: "",
    city: "",
    userEmail: "",
    profilePicUrl: ""
  });
  const [useCustomLocation, setUseCustomLocation] = useState(false);

  useEffect(() => {
    if (profile) {
      setForm({
        userName: profile.userName || "",
        headline: profile.headline || "",
        bio: profile.bio || "",
        gender: profile.gender || "",
        birthDate: profile.birthDate || "",
        country: profile.country || "",
        city: profile.city || "",
        userEmail: profile.userEmail || "",
        profilePicUrl: profile.profilePicUrl || ""
      });
      setUseCustomLocation(false);
    }
  }, [profile]);

  if (!open) return null;

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function handleSubmit(e) {
    e.preventDefault();
    onSave(form);
  }

  return (
    <div style={{ position: "fixed", top: 0, left: 0, width: "100vw", height: "100vh", background: "#0008", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }}>
      <form onSubmit={handleSubmit} style={{ background: "#fff", padding: 32, borderRadius: 12, minWidth: 320, boxShadow: "0 2px 16px #0003" }}>
        <h2>Edit Profile Info</h2>
        <label>Name:<br /><input name="userName" value={form.userName} onChange={handleChange} required /></label><br />
        <label>Headline:<br /><input name="headline" value={form.headline} onChange={handleChange} /></label><br />
        <label>Bio:<br /><textarea name="bio" value={form.bio} onChange={handleChange} /></label><br />
        <label>Gender:<br /><input name="gender" value={form.gender} onChange={handleChange} /></label><br />
        <label>Birth Date:<br /><input name="birthDate" type="date" value={form.birthDate} onChange={handleChange} /></label><br />
        <label>Location:<br />
          {!useCustomLocation ? (
            <>
              <select
                value={form.country + '|' + form.city}
                onChange={e => {
                  if (e.target.value === '__custom__') {
                    setUseCustomLocation(true);
                  } else {
                    const [country, city] = e.target.value.split('|');
                    setForm({ ...form, country, city });
                  }
                }}
              >
                <option value="">Select location</option>
                {locations.map(loc => (
                  <option key={loc.locationId} value={loc.country + '|' + loc.city}>
                    {loc.country}, {loc.city}
                  </option>
                ))}
                <option value="__custom__">Other...</option>
              </select>
            </>
          ) : (
            <>
              <input name="country" placeholder="Country" value={form.country} onChange={handleChange} />
              <input name="city" placeholder="City" value={form.city} onChange={handleChange} />
              <button type="button" onClick={() => setUseCustomLocation(false)} style={{ marginLeft: 8 }}>Back</button>
            </>
          )}
        </label><br />
        <label>Email:<br /><input name="userEmail" type="email" value={form.userEmail} onChange={handleChange} /></label><br />
        <label>Profile Picture URL:<br /><input name="profilePicUrl" value={form.profilePicUrl} onChange={handleChange} /></label><br />
        <div style={{ marginTop: 16 }}>
          <button type="submit">Save</button>
          <button type="button" onClick={onCancel} style={{ marginLeft: 8 }}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
