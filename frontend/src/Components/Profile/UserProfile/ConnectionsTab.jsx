import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../../../config";
import Navbar from "../../Navbar";

const API_BASE = `${API_BASE_URL}/api`;

export default function ConnectionsTab() {
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');
  const [sentRequests, setSentRequests] = useState([]);
  const [receivedRequests, setReceivedRequests] = useState([]);
  const [connections, setConnections] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchConnections();
  }, []);

  const fetchConnections = async () => {
    try {
      const token = localStorage.getItem('authToken');
      const headers = { 'Authorization': `Bearer ${token}` };

      const [pendingRes, acceptedRes] = await Promise.all([
        fetch(`${API_BASE}/connections/pending?userId=${userId}`, { headers }),
        fetch(`${API_BASE}/connections/accepted?userId=${userId}`, { headers })
      ]);

      const pending = await pendingRes.json();
      const accepted = await acceptedRes.json();

      const sent = pending.filter(c => c.senderId == userId);
      const received = pending.filter(c => c.receiverId == userId);

      const sentWithProfiles = await Promise.all(sent.map(async (c) => {
        const profile = await fetchProfile(c.receiverId);
        return { ...c, name: c.receiverName, headline: profile?.headline || 'No headline' };
      }));

      const receivedWithProfiles = await Promise.all(received.map(async (c) => {
        const profile = await fetchProfile(c.senderId);
        return { ...c, name: c.senderName, headline: profile?.headline || 'No headline' };
      }));

      const connectionsWithProfiles = await Promise.all(accepted.map(async (c) => {
        const otherId = c.senderId == userId ? c.receiverId : c.senderId;
        const otherName = c.senderId == userId ? c.receiverName : c.senderName;
        const profile = await fetchProfile(otherId);
        return { ...c, name: otherName, headline: profile?.headline || 'No headline', otherId };
      }));

      setSentRequests(sentWithProfiles);
      setReceivedRequests(receivedWithProfiles);
      setConnections(connectionsWithProfiles);
    } catch (err) {
      console.error('Failed to fetch connections:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchProfile = async (userId) => {
    try {
      const token = localStorage.getItem('authToken');
      const res = await fetch(`${API_BASE}/profiles/user/${userId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) return await res.json();
    } catch (err) {
      console.error('Failed to fetch profile:', err);
    }
    return null;
  };

  const handleCancel = async (connectionId) => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE}/connections/${connectionId}/cancel?userId=${userId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      fetchConnections();
    } catch (err) {
      alert('Failed to cancel request');
    }
  };

  const handleAccept = async (connectionId) => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE}/connections/${connectionId}/accept?userId=${userId}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      fetchConnections();
    } catch (err) {
      alert('Failed to accept request');
    }
  };

  const handleReject = async (connectionId) => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE}/connections/${connectionId}/reject?userId=${userId}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      fetchConnections();
    } catch (err) {
      alert('Failed to reject request');
    }
  };

  const handleRemove = async (connectionId) => {
    try {
      const token = localStorage.getItem('authToken');
      await fetch(`${API_BASE}/connections/${connectionId}/remove?userId=${userId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      fetchConnections();
    } catch (err) {
      alert('Failed to remove connection');
    }
  };

  if (loading) return <div>Loading connections...</div>;

  return (
    <>
      <Navbar />
      <div style={{ maxWidth: '900px', margin: '80px auto 0', backgroundColor: 'white', minHeight: '100vh' }}>
        <div style={{ padding: '32px 40px' }}>
          <div style={{ marginBottom: '32px' }}>
            <h1 style={{ fontSize: '32px', fontWeight: '700', color: '#000000', margin: '0 0 8px 0', letterSpacing: '-0.5px' }}>My Network</h1>
            <p style={{ fontSize: '15px', color: '#666666', margin: '0' }}>Manage your professional connections</p>
          </div>
      <section style={{ backgroundColor: 'white', border: '1px solid #e0e0e0', borderRadius: '12px', marginBottom: '24px', overflow: 'hidden' }}>
        <div style={{ padding: '24px 24px 16px', borderBottom: '1px solid #f3f6f9' }}>
          <h3 style={{ fontSize: '20px', fontWeight: '600', color: '#1a1a1a', margin: '0 0 8px 0' }}>Sent Requests</h3>
          <p style={{ fontSize: '14px', color: '#666666', margin: '0' }}>Pending connection requests you sent</p>
        </div>
        <div style={{ padding: '0 24px 24px' }}>
          {sentRequests.length === 0 ? (
            <div style={{ padding: '32px 0', textAlign: 'center', color: '#666666', fontSize: '15px' }}>No pending sent requests</div>
          ) : (
            sentRequests.map(req => (
              <div key={req.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 0', borderBottom: '1px solid #f3f6f9' }}>
                <div style={{ flex: 1 }}>
                  <div 
                    style={{ fontWeight: '600', color: '#1976d2', cursor: 'pointer', fontSize: '16px', marginBottom: '4px' }}
                    onClick={() => {
                      navigate(`/profile/${req.receiverId}`);
                      window.location.reload();
                    }}
                  >{req.name}</div>
                  <div style={{ fontSize: '14px', color: '#666666' }}>{req.headline}</div>
                </div>
                <button 
                  style={{ 
                    padding: '8px 16px', 
                    fontSize: '14px', 
                    fontWeight: '500', 
                    color: '#666666', 
                    backgroundColor: 'white', 
                    border: '1px solid #d0d0d0', 
                    borderRadius: '8px', 
                    cursor: 'pointer',
                    transition: 'all 0.2s ease'
                  }}
                  onMouseOver={(e) => {
                    e.target.style.backgroundColor = '#f8f9fa';
                    e.target.style.borderColor = '#1976d2';
                  }}
                  onMouseOut={(e) => {
                    e.target.style.backgroundColor = 'white';
                    e.target.style.borderColor = '#d0d0d0';
                  }}
                  onClick={() => handleCancel(req.id)}
                >Cancel</button>
              </div>
            ))
          )}
        </div>
      </section>

      <section style={{ backgroundColor: 'white', border: '1px solid #e0e0e0', borderRadius: '12px', marginBottom: '24px', overflow: 'hidden' }}>
        <div style={{ padding: '24px 24px 16px', borderBottom: '1px solid #f3f6f9' }}>
          <h3 style={{ fontSize: '20px', fontWeight: '600', color: '#1a1a1a', margin: '0 0 8px 0' }}>Received Requests</h3>
          <p style={{ fontSize: '14px', color: '#666666', margin: '0' }}>Connection requests waiting for your response</p>
        </div>
        <div style={{ padding: '0 24px 24px' }}>
          {receivedRequests.length === 0 ? (
            <div style={{ padding: '32px 0', textAlign: 'center', color: '#666666', fontSize: '15px' }}>No pending received requests</div>
          ) : (
            receivedRequests.map(req => (
              <div key={req.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 0', borderBottom: '1px solid #f3f6f9' }}>
                <div style={{ flex: 1 }}>
                  <div 
                    style={{ fontWeight: '600', color: '#1976d2', cursor: 'pointer', fontSize: '16px', marginBottom: '4px' }}
                    onClick={() => {
                      navigate(`/profile/${req.senderId}`);
                      window.location.reload();
                    }}
                  >{req.name}</div>
                  <div style={{ fontSize: '14px', color: '#666666' }}>{req.headline}</div>
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button 
                    style={{ 
                      padding: '8px 16px', 
                      fontSize: '14px', 
                      fontWeight: '500', 
                      color: 'white', 
                      backgroundColor: '#1976d2', 
                      border: 'none', 
                      borderRadius: '8px', 
                      cursor: 'pointer',
                      transition: 'all 0.2s ease'
                    }}
                    onMouseOver={(e) => {
                      e.target.style.backgroundColor = '#1565c0';
                      e.target.style.transform = 'translateY(-1px)';
                    }}
                    onMouseOut={(e) => {
                      e.target.style.backgroundColor = '#1976d2';
                      e.target.style.transform = 'translateY(0)';
                    }}
                    onClick={() => handleAccept(req.id)}
                  >Accept</button>
                  <button 
                    style={{ 
                      padding: '8px 16px', 
                      fontSize: '14px', 
                      fontWeight: '500', 
                      color: '#666666', 
                      backgroundColor: 'white', 
                      border: '1px solid #d0d0d0', 
                      borderRadius: '8px', 
                      cursor: 'pointer',
                      transition: 'all 0.2s ease'
                    }}
                    onMouseOver={(e) => {
                      e.target.style.backgroundColor = '#f8f9fa';
                      e.target.style.borderColor = '#1976d2';
                    }}
                    onMouseOut={(e) => {
                      e.target.style.backgroundColor = 'white';
                      e.target.style.borderColor = '#d0d0d0';
                    }}
                    onClick={() => handleReject(req.id)}
                  >Reject</button>
                </div>
              </div>
            ))
          )}
        </div>
      </section>

      <section style={{ backgroundColor: 'white', border: '1px solid #e0e0e0', borderRadius: '12px', marginBottom: '24px', overflow: 'hidden' }}>
        <div style={{ padding: '24px 24px 16px', borderBottom: '1px solid #f3f6f9' }}>
          <h3 style={{ fontSize: '20px', fontWeight: '600', color: '#1a1a1a', margin: '0 0 8px 0' }}>All Connections</h3>
          <p style={{ fontSize: '14px', color: '#666666', margin: '0' }}>{connections.length} connection{connections.length !== 1 ? 's' : ''}</p>
        </div>
        <div style={{ padding: '0 24px 24px' }}>
          {connections.length === 0 ? (
            <div style={{ padding: '32px 0', textAlign: 'center', color: '#666666', fontSize: '15px' }}>No connections yet</div>
          ) : (
            connections.map(conn => (
              <div key={conn.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 0', borderBottom: '1px solid #f3f6f9' }}>
                <div style={{ flex: 1 }}>
                  <div 
                    style={{ fontWeight: '600', color: '#1976d2', cursor: 'pointer', fontSize: '16px', marginBottom: '4px' }}
                    onClick={() => {
                      navigate(`/profile/${conn.otherId}`);
                      window.location.reload();
                    }}
                  >{conn.name}</div>
                  <div style={{ fontSize: '14px', color: '#666666' }}>{conn.headline}</div>
                </div>
                <button 
                  style={{ 
                    padding: '8px 16px', 
                    fontSize: '14px', 
                    fontWeight: '500', 
                    color: '#dc3545', 
                    backgroundColor: 'white', 
                    border: '1px solid #dc3545', 
                    borderRadius: '8px', 
                    cursor: 'pointer',
                    transition: 'all 0.2s ease'
                  }}
                  onMouseOver={(e) => {
                    e.target.style.backgroundColor = '#dc3545';
                    e.target.style.color = 'white';
                  }}
                  onMouseOut={(e) => {
                    e.target.style.backgroundColor = 'white';
                    e.target.style.color = '#dc3545';
                  }}
                  onClick={() => handleRemove(conn.id)}
                >Remove</button>
              </div>
            ))
          )}
        </div>
      </section>
        </div>
      </div>
    </>
  );
}
