import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../../../config";

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
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      <section className="up-card">
        <div className="up-card-header">
          <div className="up-card-title">
            <h3>Sent Requests</h3>
            <div className="up-card-sub">Pending connection requests you sent</div>
          </div>
        </div>
        <div className="up-card-body">
          {sentRequests.length === 0 ? (
            <p style={{ color: '#6b7280' }}>No pending sent requests</p>
          ) : (
            sentRequests.map(req => (
              <div key={req.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 0', borderBottom: '1px solid #f3f6f9' }}>
                <div>
                  <div 
                    style={{ fontWeight: 600, color: '#0066cc', cursor: 'pointer' }}
                    onClick={() => {
                      navigate(`/profile/${req.receiverId}`);
                      window.location.reload();
                    }}
                  >{req.name}</div>
                  <div style={{ fontSize: '13px', color: '#6b7280' }}>{req.headline}</div>
                </div>
                <button className="btn btn-outline btn-sm" onClick={() => handleCancel(req.id)}>Cancel</button>
              </div>
            ))
          )}
        </div>
      </section>

      <section className="up-card">
        <div className="up-card-header">
          <div className="up-card-title">
            <h3>Received Requests</h3>
            <div className="up-card-sub">Connection requests waiting for your response</div>
          </div>
        </div>
        <div className="up-card-body">
          {receivedRequests.length === 0 ? (
            <p style={{ color: '#6b7280' }}>No pending received requests</p>
          ) : (
            receivedRequests.map(req => (
              <div key={req.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 0', borderBottom: '1px solid #f3f6f9' }}>
                <div>
                  <div 
                    style={{ fontWeight: 600, color: '#0066cc', cursor: 'pointer' }}
                    onClick={() => {
                      navigate(`/profile/${req.senderId}`);
                      window.location.reload();
                    }}
                  >{req.name}</div>
                  <div style={{ fontSize: '13px', color: '#6b7280' }}>{req.headline}</div>
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <button className="btn btn-primary btn-sm" onClick={() => handleAccept(req.id)}>Accept</button>
                  <button className="btn btn-outline btn-sm" onClick={() => handleReject(req.id)}>Reject</button>
                </div>
              </div>
            ))
          )}
        </div>
      </section>

      <section className="up-card">
        <div className="up-card-header">
          <div className="up-card-title">
            <h3>All Connections</h3>
            <div className="up-card-sub">{connections.length} connection{connections.length !== 1 ? 's' : ''}</div>
          </div>
        </div>
        <div className="up-card-body">
          {connections.length === 0 ? (
            <p style={{ color: '#6b7280' }}>No connections yet</p>
          ) : (
            connections.map(conn => (
              <div key={conn.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 0', borderBottom: '1px solid #f3f6f9' }}>
                <div>
                  <div 
                    style={{ fontWeight: 600, color: '#0066cc', cursor: 'pointer' }}
                    onClick={() => {
                      navigate(`/profile/${conn.otherId}`);
                      window.location.reload();
                    }}
                  >{conn.name}</div>
                  <div style={{ fontSize: '13px', color: '#6b7280' }}>{conn.headline}</div>
                </div>
                <button className="btn btn-danger btn-sm" onClick={() => handleRemove(conn.id)}>Remove</button>
              </div>
            ))
          )}
        </div>
      </section>
    </div>
  );
}
