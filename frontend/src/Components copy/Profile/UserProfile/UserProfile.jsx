import React, { useEffect, useState } from "react";
import ProfileInfo from "./ProfileInfo";
import ExperienceSection from "./ExperienceSection";
import SkillsSection from "./SkillsSection";
import ProjectsSection from "./ProjectsSection";
import EducationSection from "./EducationSection";
import ProfileInfoForm from "./ProfileInfoForm";
import ExperienceForm from "./ExperienceForm";
import SkillForm from "./SkillForm";
import ProjectForm from "./ProjectForm";
import EducationForm from "./EducationForm";
import "./UserProfile.css";
import { API_BASE_URL } from "../../../config";


const API_BASE = `${API_BASE_URL}/api/profiles`;

export default function UserProfile({ profileId = 1 }) {
  const [profile, setProfile] = useState(null);
  const [locations, setLocations] = useState([]);
  const [experience, setExperience] = useState([]);
  const [skills, setSkills] = useState([]);
  const [projects, setProjects] = useState([]);
  const [education, setEducation] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Edit mode states
  const [editProfile, setEditProfile] = useState(false);
  const [editExperienceId, setEditExperienceId] = useState(null);
  const [editSkillId, setEditSkillId] = useState(null);
  const [editProjectId, setEditProjectId] = useState(null);
  const [editEducationId, setEditEducationId] = useState(null);

  useEffect(() => {
    async function fetchAll() {
      setLoading(true);
      try {
        const [profileRes, expRes, skillRes, projRes, eduRes, locRes] = await Promise.all([
          fetch(`${API_BASE}/${profileId}`),
          fetch(`${API_BASE}/${profileId}/experience`),
          fetch(`${API_BASE}/${profileId}/skills`),
          fetch(`${API_BASE}/${profileId}/projects`),
          fetch(`${API_BASE}/${profileId}/education`),
          fetch(`${API_BASE_URL}/api/locations`)
        ]);
        setProfile(await profileRes.json());
        setExperience(await expRes.json());
        setSkills(await skillRes.json());
        setProjects(await projRes.json());
        setEducation(await eduRes.json());
        setLocations(await locRes.json());
        setError(null);
      } catch (err) {
        setError("Failed to fetch profile data");
      }
      setLoading(false);
    }
    fetchAll();
  }, [profileId]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div style={{ color: "red" }}>{error}</div>;
  if (!profile) return <div>No profile found.</div>;

  // Handler stubs for edit/add/delete
  const handleEditProfile = () => setEditProfile(true);
  const handleSaveProfile = async (form) => {
    try {
      let locationId = profile.locationId;
      // If location changed, find or create location
      if (form.country !== profile.country || form.city !== profile.city) {
        // Try to find existing location
        let loc = locations.find(l => l.country === form.country && l.city === form.city);
        if (!loc) {
          // Create new location
          const locRes = await fetch(`${API_BASE_URL}/api/locations`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ country: form.country, city: form.city })
          });
          if (!locRes.ok) throw new Error('Failed to create location');
          loc = await locRes.json();
          // Refresh locations
          const allLocRes = await fetch(`${API_BASE_URL}/api/locations`);
          setLocations(await allLocRes.json());
        }
        locationId = loc.locationId;
      }
      // Update profile with new locationId
      const profilePayload = { ...form, locationId };
      const res = await fetch(`${API_BASE}/${profileId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(profilePayload)
      });
      if (!res.ok) throw new Error('Failed to update profile');
      const updated = await res.json();
      setProfile(updated);
      setEditProfile(false);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelProfile = () => setEditProfile(false);

  const handleEditExperience = id => setEditExperienceId(id);
  const handleAddExperience = () => setEditExperienceId("new");
  const handleSaveExperience = async (form) => {
    try {
      let url = `${API_BASE}/${profileId}/experience`;
      let method = 'POST';
      if (editExperienceId && editExperienceId !== 'new') {
        url += `/${editExperienceId}`;
        method = 'PUT';
      }
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });
      if (!res.ok) throw new Error('Failed to save experience');
      // Refresh experience list
      const expRes = await fetch(`${API_BASE}/${profileId}/experience`);
      setExperience(await expRes.json());
      setEditExperienceId(null);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelExperience = () => setEditExperienceId(null);
  const handleDeleteExperience = async (id) => {
    try {
      const res = await fetch(`${API_BASE}/${profileId}/experience/${id}`, { method: 'DELETE' });
      if (!res.ok) throw new Error('Failed to delete experience');
      const expRes = await fetch(`${API_BASE}/${profileId}/experience`);
      setExperience(await expRes.json());
    } catch (err) {
      alert(err.message);
    }
  };

  const handleEditSkill = id => setEditSkillId(id);
  const handleAddSkill = () => setEditSkillId("new");
  const handleSaveSkill = async (form) => {
    try {
      let url = `${API_BASE}/${profileId}/skills`;
      let method = 'POST';
      if (editSkillId && editSkillId !== 'new') {
        url += `/${editSkillId}`;
        method = 'PUT';
      }
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });
      if (!res.ok) throw new Error('Failed to save skill');
      const skillRes = await fetch(`${API_BASE}/${profileId}/skills`);
      setSkills(await skillRes.json());
      setEditSkillId(null);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelSkill = () => setEditSkillId(null);
  const handleDeleteSkill = async (id) => {
    try {
      const res = await fetch(`${API_BASE}/${profileId}/skills/${id}`, { method: 'DELETE' });
      if (!res.ok) throw new Error('Failed to delete skill');
      const skillRes = await fetch(`${API_BASE}/${profileId}/skills`);
      setSkills(await skillRes.json());
    } catch (err) {
      alert(err.message);
    }
  };

  const handleEditProject = id => setEditProjectId(id);
  const handleAddProject = () => setEditProjectId("new");
  const handleSaveProject = async (form) => {
    try {
      let url = `${API_BASE}/${profileId}/projects`;
      let method = 'POST';
      if (editProjectId && editProjectId !== 'new') {
        url += `/${editProjectId}`;
        method = 'PUT';
      }
      // Convert empty date strings to null
      const payload = {
        ...form,
        startDate: form.startDate ? form.startDate : null,
        endDate: form.endDate ? form.endDate : null
      };
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error('Failed to save project');
      const projRes = await fetch(`${API_BASE}/${profileId}/projects`);
      setProjects(await projRes.json());
      setEditProjectId(null);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelProject = () => setEditProjectId(null);
  const handleDeleteProject = async (id) => {
    try {
      const res = await fetch(`${API_BASE}/${profileId}/projects/${id}`, { method: 'DELETE' });
      if (!res.ok) throw new Error('Failed to delete project');
      const projRes = await fetch(`${API_BASE}/${profileId}/projects`);
      setProjects(await projRes.json());
    } catch (err) {
      alert(err.message);
    }
  };

  const handleEditEducation = id => setEditEducationId(id);
  const handleAddEducation = () => setEditEducationId("new");
  const handleSaveEducation = async (form) => {
    try {
      let url = `${API_BASE}/${profileId}/education`;
      let method = 'POST';
      if (editEducationId && editEducationId !== 'new') {
        url += `/${editEducationId}`;
        method = 'PUT';
      }
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });
      if (!res.ok) throw new Error('Failed to save education');
      const eduRes = await fetch(`${API_BASE}/${profileId}/education`);
      setEducation(await eduRes.json());
      setEditEducationId(null);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelEducation = () => setEditEducationId(null);
  const handleDeleteEducation = async (id) => {
    try {
      const res = await fetch(`${API_BASE}/${profileId}/education/${id}`, { method: 'DELETE' });
      if (!res.ok) throw new Error('Failed to delete education');
      const eduRes = await fetch(`${API_BASE}/${profileId}/education`);
      setEducation(await eduRes.json());
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="up-page" style={{ fontFamily: "system-ui", maxWidth: 980, margin: "2rem auto" }}>
      <div className="profile-container" style={{ background: "#fff", borderRadius: 12, boxShadow: "0 2px 16px #0002", padding: 24 }}>
        <ProfileInfo profile={profile} onEdit={handleEditProfile} />

        <div className="up-sections">
          {/* Experience Card */}
          <section className="up-card">
            <div className="up-card-header">
              <div className="up-card-title">
                <h3>Experience</h3>
                <div className="up-card-sub">Professional roles & responsibilities</div>
              </div>
              <div className="up-card-actions">
                {/* keep header Add (styled) and remove Manage */}
                <button className="btn btn-outline btn-sm" onClick={handleAddExperience}>+ Add</button>
              </div>
            </div>
            <div className="up-card-body">
              <ExperienceSection experience={experience} onAdd={handleAddExperience} onEdit={handleEditExperience} onDelete={handleDeleteExperience} />
            </div>
          </section>

          {/* Education Card */}
          <section className="up-card">
            <div className="up-card-header">
              <div className="up-card-title">
                <h3>Education</h3>
                <div className="up-card-sub">Degrees, certifications and institutions</div>
              </div>
              <div className="up-card-actions">
                <button className="btn btn-outline btn-sm" onClick={handleAddEducation}>+ Add</button>
              </div>
            </div>
            <div className="up-card-body">
              <EducationSection education={education} onAdd={handleAddEducation} onEdit={handleEditEducation} onDelete={handleDeleteEducation} />
            </div>
          </section>

          {/* Skills Card */}
          <section className="up-card">
            <div className="up-card-header">
              <div className="up-card-title">
                <h3>Skills</h3>
                <div className="up-card-sub">Core skills, tools & proficiencies</div>
              </div>
              <div className="up-card-actions">
                <button className="btn btn-outline btn-sm" onClick={handleAddSkill}>+ Add</button>
              </div>
            </div>
            <div className="up-card-body">
              <SkillsSection skills={skills} onAdd={handleAddSkill} onEdit={handleEditSkill} onDelete={handleDeleteSkill} />
            </div>
          </section>

          {/* Projects Card */}
          <section className="up-card">
            <div className="up-card-header">
              <div className="up-card-title">
                <h3>Projects</h3>
                <div className="up-card-sub">Selected projects and highlights</div>
              </div>
              <div className="up-card-actions">
                <button className="btn btn-outline btn-sm" onClick={handleAddProject}>+ Add</button>
              </div>
            </div>
            <div className="up-card-body">
              <ProjectsSection projects={projects} onAdd={handleAddProject} onEdit={handleEditProject} onDelete={handleDeleteProject} />
            </div>
          </section>
        </div>

        {/* Modals for editing/adding fields */}
        <ProfileInfoForm open={editProfile} profile={profile} locations={locations} onSave={handleSaveProfile} onCancel={handleCancelProfile} />
        <ExperienceForm open={!!editExperienceId} experience={experience.find(e => e.id === editExperienceId)} onSave={handleSaveExperience} onCancel={handleCancelExperience} />
        <SkillForm open={!!editSkillId} skill={skills.find(s => s.id === editSkillId)} onSave={handleSaveSkill} onCancel={handleCancelSkill} />
        <ProjectForm open={!!editProjectId} project={projects.find(p => p.id === editProjectId)} onSave={handleSaveProject} onCancel={handleCancelProject} />
        <EducationForm open={!!editEducationId} education={education.find(e => e.id === editEducationId)} onSave={handleSaveEducation} onCancel={handleCancelEducation} />
      </div>
    </div>
  );
}
