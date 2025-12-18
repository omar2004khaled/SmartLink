import React, { useEffect, useState } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import ConnectionsTab from "./ConnectionsTab";
import Navbar from "../../Navbar";
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

export default function UserProfile() {
  const navigate = useNavigate();
  const { userId: urlUserId } = useParams();
  const loggedInUserId = localStorage.getItem('userId');
  const userId = urlUserId || loggedInUserId;
  const isOwnProfile = !urlUserId || urlUserId === loggedInUserId;
  const [profileId, setProfileId] = useState(null);
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
  const [connectionStatus, setConnectionStatus] = useState(null);
  const [connectionId, setConnectionId] = useState(null);
  const [activeTab, setActiveTab] = useState('profile');
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const tab = params.get('tab');
    if (tab === 'connections' && isOwnProfile) {
      setActiveTab('connections');
    }
  }, [location.search, isOwnProfile]);

  useEffect(() => {
    async function fetchProfileId() {
      if (!userId) {
        navigate('/login');
        return;
      }
      try {
        const token = localStorage.getItem('authToken');
        const res = await fetch(`${API_BASE}/user/${userId}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
          const data = await res.json();
          setProfileId(data.profileId);
        } else {
          setError("Profile not found");
        }
      } catch (err) {
        setError("Failed to fetch profile");
      }
    }
    fetchProfileId();
  }, [userId, navigate]);

  useEffect(() => {
    async function fetchAll() {
      if (!profileId) return;
      setLoading(true);
      try {
        const token = localStorage.getItem('authToken');
        const headers = { 'Authorization': `Bearer ${token}` };
        const [profileRes, expRes, skillRes, projRes, eduRes, locRes] = await Promise.all([
          fetch(`${API_BASE}/${profileId}`, { headers }),
          fetch(`${API_BASE}/${profileId}/experience`, { headers }),
          fetch(`${API_BASE}/${profileId}/skills`, { headers }),
          fetch(`${API_BASE}/${profileId}/projects`, { headers }),
          fetch(`${API_BASE}/${profileId}/education`, { headers }),
          fetch(`${API_BASE_URL}/api/locations`, { headers })
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
    if (!isOwnProfile && profileId) {
      checkConnectionStatus();
    }
  }, [profileId, isOwnProfile]);

  const checkConnectionStatus = async () => {
    try {
      const token = localStorage.getItem('authToken');
      const [pendingRes, acceptedRes] = await Promise.all([
        fetch(`${API_BASE_URL}/api/connections/pending?userId=${loggedInUserId}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        }),
        fetch(`${API_BASE_URL}/api/connections/accepted?userId=${loggedInUserId}`, {
          headers: { 'Authorization': `Bearer ${token}` }
        })
      ]);
      const pending = await pendingRes.json();
      const accepted = await acceptedRes.json();
      
      const pendingConn = pending.find(c => 
        (c.senderId == loggedInUserId && c.receiverId == userId) ||
        (c.senderId == userId && c.receiverId == loggedInUserId)
      );
      const acceptedConn = accepted.find(c => 
        (c.senderId == loggedInUserId && c.receiverId == userId) ||
        (c.senderId == userId && c.receiverId == loggedInUserId)
      );
      
      if (acceptedConn) {
        setConnectionStatus('ACCEPTED');
        setConnectionId(acceptedConn.id);
      } else if (pendingConn) {
        if (pendingConn.senderId == loggedInUserId) {
          setConnectionStatus('PENDING_SENT');
        } else {
          setConnectionStatus('PENDING_RECEIVED');
        }
        setConnectionId(pendingConn.id);
      } else {
        setConnectionStatus('NONE');
      }
    } catch (err) {
      console.error('Failed to check connection status:', err);
    }
  };

  const handleConnect = async () => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE_URL}/api/connections/send`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ senderId: parseInt(loggedInUserId), receiverId: parseInt(userId) })
      });
      checkConnectionStatus();
    } catch (err) {
      alert('Failed to send connection request');
    }
  };

  const handleCancelConnection = async () => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE_URL}/api/connections/${connectionId}/cancel?userId=${loggedInUserId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      checkConnectionStatus();
    } catch (err) {
      alert('Failed to cancel connection');
    }
  };

  const handleAcceptConnection = async () => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE_URL}/api/connections/${connectionId}/accept?userId=${loggedInUserId}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      checkConnectionStatus();
    } catch (err) {
      alert('Failed to accept connection');
    }
  };

  const handleRejectConnection = async () => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE_URL}/api/connections/${connectionId}/reject?userId=${loggedInUserId}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      checkConnectionStatus();
    } catch (err) {
      alert('Failed to reject connection');
    }
  };

  const handleRemoveConnection = async () => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE_URL}/api/connections/${connectionId}/remove?userId=${loggedInUserId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      checkConnectionStatus();
    } catch (err) {
      alert('Failed to remove connection');
    }
  };

  const getConnectionHandler = () => {
    if (connectionStatus === 'NONE') return handleConnect;
    if (connectionStatus === 'PENDING_SENT') return handleCancelConnection;
    if (connectionStatus === 'PENDING_RECEIVED') return { accept: handleAcceptConnection, reject: handleRejectConnection };
    if (connectionStatus === 'ACCEPTED') return handleRemoveConnection;
    return null;
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div style={{ color: "red" }}>{error}</div>;
  if (!profile) return <div>No profile found.</div>;

  // Handler stubs for edit/add/delete (only for own profile)
  const handleEditProfile = () => isOwnProfile && setEditProfile(true);
  const handleSaveProfile = async (form) => {
    try {
      console.log('Starting profile save with form:', form);
      const token = localStorage.getItem('authToken');
      let locationId = profile.locationId;
      
      // If location changed, find or create location
      if (form.country && form.city && (form.country !== profile.country || form.city !== profile.city)) {
        console.log('Location changed, processing...');
        // Try to find existing location
        let loc = locations.find(l => l.country === form.country && l.city === form.city);
        if (!loc) {
          console.log('Creating new location...');
          // Create new location
          const locRes = await fetch(`${API_BASE_URL}/api/locations`, {
            method: 'POST',
            headers: { 
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ country: form.country, city: form.city })
          });
          if (!locRes.ok) {
            const errorText = await locRes.text();
            console.error('Location creation failed:', errorText);
            throw new Error('Failed to create location: ' + errorText);
          }
          loc = await locRes.json();
          console.log('New location created:', loc);
          // Refresh locations
          const allLocRes = await fetch(`${API_BASE_URL}/api/locations`, {
            headers: { 'Authorization': `Bearer ${token}` }
          });
          setLocations(await allLocRes.json());
        }
        locationId = loc.locationId;
      }
      
      // Update profile with only backend-expected fields
      const profilePayload = {
        profilePicUrl: form.profilePicUrl || null,
        bio: form.bio || null,
        headline: form.headline || null,
        gender: form.gender || null,
        birthDate: form.birthDate || null,
        userId: parseInt(userId),
        locationId: locationId
      };
      
      console.log('Sending profile update:', profilePayload);
      console.log('URL:', `${API_BASE}/${profileId}`);
      
      const res = await fetch(`${API_BASE}/${profileId}`, {
        method: 'PUT',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(profilePayload)
      });
      
      console.log('Response status:', res.status);
      console.log('Response headers:', res.headers);
      
      if (!res.ok) {
        const errorText = await res.text();
        console.error('Profile update failed:', errorText);
        throw new Error(`Failed to update profile (${res.status}): ${errorText}`);
      }
      
      const updated = await res.json();
      console.log('Profile updated successfully:', updated);
      setProfile(updated);
      setEditProfile(false);
    } catch (err) {
      console.error('Profile update error:', err);
      alert(`Profile update failed: ${err.message}`);
    }
  };
  const handleCancelProfile = () => setEditProfile(false);

  const handleEditExperience = id => isOwnProfile && setEditExperienceId(id);
  const handleAddExperience = () => isOwnProfile && setEditExperienceId("new");
  const handleSaveExperience = async (form) => {
    try {
      const token = localStorage.getItem('authToken');
      let url = `${API_BASE}/${profileId}/experience`;
      let method = 'POST';
      if (editExperienceId && editExperienceId !== 'new') {
        url += `/${editExperienceId}`;
        method = 'PUT';
      }
      const res = await fetch(url, {
        method,
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(form)
      });
      if (!res.ok) throw new Error('Failed to save experience');
      // Refresh experience list
      const expRes = await fetch(`${API_BASE}/${profileId}/experience`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setExperience(await expRes.json());
      setEditExperienceId(null);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelExperience = () => setEditExperienceId(null);
  const handleDeleteExperience = async (id) => {
    try {
      const token = localStorage.getItem('authToken');
      const res = await fetch(`${API_BASE}/${profileId}/experience/${id}`, { 
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Failed to delete experience');
      const expRes = await fetch(`${API_BASE}/${profileId}/experience`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setExperience(await expRes.json());
    } catch (err) {
      alert(err.message);
    }
  };

  const handleEditSkill = id => isOwnProfile && setEditSkillId(id);
  const handleAddSkill = () => isOwnProfile && setEditSkillId("new");
  const handleSaveSkill = async (form) => {
    try {
      const token = localStorage.getItem('authToken');
      let url = `${API_BASE}/${profileId}/skills`;
      let method = 'POST';
      if (editSkillId && editSkillId !== 'new') {
        url += `/${editSkillId}`;
        method = 'PUT';
      }
      const res = await fetch(url, {
        method,
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(form)
      });
      if (!res.ok) throw new Error('Failed to save skill');
      const skillRes = await fetch(`${API_BASE}/${profileId}/skills`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setSkills(await skillRes.json());
      setEditSkillId(null);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelSkill = () => setEditSkillId(null);
  const handleDeleteSkill = async (id) => {
    try {
      const token = localStorage.getItem('authToken');
      const res = await fetch(`${API_BASE}/${profileId}/skills/${id}`, { 
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Failed to delete skill');
      const skillRes = await fetch(`${API_BASE}/${profileId}/skills`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setSkills(await skillRes.json());
    } catch (err) {
      alert(err.message);
    }
  };

  const handleEditProject = id => isOwnProfile && setEditProjectId(id);
  const handleAddProject = () => isOwnProfile && setEditProjectId("new");
  const handleSaveProject = async (form) => {
    try {
      const token = localStorage.getItem('authToken');
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
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error('Failed to save project');
      const projRes = await fetch(`${API_BASE}/${profileId}/projects`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setProjects(await projRes.json());
      setEditProjectId(null);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelProject = () => setEditProjectId(null);
  const handleDeleteProject = async (id) => {
    try {
      const token = localStorage.getItem('authToken');
      const res = await fetch(`${API_BASE}/${profileId}/projects/${id}`, { 
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Failed to delete project');
      const projRes = await fetch(`${API_BASE}/${profileId}/projects`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setProjects(await projRes.json());
    } catch (err) {
      alert(err.message);
    }
  };

  const handleEditEducation = id => isOwnProfile && setEditEducationId(id);
  const handleAddEducation = () => isOwnProfile && setEditEducationId("new");
  const handleSaveEducation = async (form) => {
    try {
      const token = localStorage.getItem('authToken');
      let url = `${API_BASE}/${profileId}/education`;
      let method = 'POST';
      if (editEducationId && editEducationId !== 'new') {
        url += `/${editEducationId}`;
        method = 'PUT';
      }
      const res = await fetch(url, {
        method,
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(form)
      });
      if (!res.ok) throw new Error('Failed to save education');
      const eduRes = await fetch(`${API_BASE}/${profileId}/education`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setEducation(await eduRes.json());
      setEditEducationId(null);
    } catch (err) {
      alert(err.message);
    }
  };
  const handleCancelEducation = () => setEditEducationId(null);
  const handleDeleteEducation = async (id) => {
    try {
      const token = localStorage.getItem('authToken');
      const res = await fetch(`${API_BASE}/${profileId}/education/${id}`, { 
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Failed to delete education');
      const eduRes = await fetch(`${API_BASE}/${profileId}/education`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setEducation(await eduRes.json());
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div>
      <Navbar />
      <div className="up-page">
        <div className="user-profile-container">
          <div className="profile-container">

        
        {activeTab === 'profile' && (
          <>
            <ProfileInfo 
              profile={profile} 
              onEdit={isOwnProfile ? handleEditProfile : null}
              onConnect={!isOwnProfile ? getConnectionHandler() : null}
              connectionStatus={connectionStatus}
              isOwnProfile={isOwnProfile}
            />

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
                {isOwnProfile && <button className="btn btn-outline btn-sm" onClick={handleAddExperience}>+ Add</button>}
              </div>
            </div>
            <div className="up-card-body">
              <ExperienceSection experience={experience} onAdd={isOwnProfile ? handleAddExperience : null} onEdit={isOwnProfile ? handleEditExperience : null} onDelete={isOwnProfile ? handleDeleteExperience : null} />
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
                {isOwnProfile && <button className="btn btn-outline btn-sm" onClick={handleAddEducation}>+ Add</button>}
              </div>
            </div>
            <div className="up-card-body">
              <EducationSection education={education} onAdd={isOwnProfile ? handleAddEducation : null} onEdit={isOwnProfile ? handleEditEducation : null} onDelete={isOwnProfile ? handleDeleteEducation : null} />
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
                {isOwnProfile && <button className="btn btn-outline btn-sm" onClick={handleAddSkill}>+ Add</button>}
              </div>
            </div>
            <div className="up-card-body">
              <SkillsSection skills={skills} onAdd={isOwnProfile ? handleAddSkill : null} onEdit={isOwnProfile ? handleEditSkill : null} onDelete={isOwnProfile ? handleDeleteSkill : null} />
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
                {isOwnProfile && <button className="btn btn-outline btn-sm" onClick={handleAddProject}>+ Add</button>}
              </div>
            </div>
            <div className="up-card-body">
              <ProjectsSection projects={projects} onAdd={isOwnProfile ? handleAddProject : null} onEdit={isOwnProfile ? handleEditProject : null} onDelete={isOwnProfile ? handleDeleteProject : null} />
            </div>
          </section>
            </div>
          </>
        )}
        
        {activeTab === 'connections' && isOwnProfile && (
          <div className="up-sections">
            <ConnectionsTab />
          </div>
        )}

        {/* Modals for editing/adding fields */}
        <ProfileInfoForm open={editProfile} profile={profile} locations={locations} onSave={handleSaveProfile} onCancel={handleCancelProfile} />
        <ExperienceForm open={!!editExperienceId} experience={experience.find(e => e.id === editExperienceId)} onSave={handleSaveExperience} onCancel={handleCancelExperience} />
        <SkillForm open={!!editSkillId} skill={skills.find(s => s.id === editSkillId)} onSave={handleSaveSkill} onCancel={handleCancelSkill} />
        <ProjectForm open={!!editProjectId} project={projects.find(p => p.id === editProjectId)} onSave={handleSaveProject} onCancel={handleCancelProject} />
        <EducationForm open={!!editEducationId} education={education.find(e => e.id === editEducationId)} onSave={handleSaveEducation} onCancel={handleCancelEducation} />
          </div>
        </div>
      </div>
    </div>  
  );
}
