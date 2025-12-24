import React, { useState } from 'react';
import { Plus } from 'lucide-react';
import CompanyNavbar from './CompanyNavbar';
import Posts from './Posts';
import PostComposotion from '../PostComposotion/PostComposotion';
import './MainPage.css';

const CompanyHome = () => {
  const [showCreatePost, setShowCreatePost] = useState(false);
  const [userId, setUserId] = useState(null);
  useEffect(() => {
    const id = localStorage.getItem('userId');
    if (id) {
      setUserId(parseInt(id));
    }
  }, []);
  return (
    <div className="home-page">
      {userId && <JobRecommendationTrigger userId={userId} />}
      <CompanyNavbar />
      
      <div className="home-content">
        <div className="home-header">
          <div className="header-info">
            <h1 className="home-title">Company Dashboard</h1>
            <p className="home-subtitle">Welcome back! Manage your company presence and connect with talent.</p>
          </div>
          
        </div>

        <div className="main-content">
          <div className="content-header">
            <h2>Recent Activity</h2>
            <button className="quick-post-btn" onClick={() => setShowCreatePost(true)}>
              <Plus size={16} />
              Share something
            </button>
          </div>
          <Posts />
        </div>
      </div>

      {showCreatePost && (
        <div className="modal-overlay" onClick={() => setShowCreatePost(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <PostComposotion onClose={() => setShowCreatePost(false)} />
          </div>
        </div>
      )}
    </div>
  );
};

export default CompanyHome;