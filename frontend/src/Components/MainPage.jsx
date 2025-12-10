import React, { useState } from 'react';
import { Plus } from 'lucide-react';
import Navbar from './Navbar';
import Posts from './Posts';
import PostComposotion from '../PostComposotion/PostComposotion';
import './MainPage.css';

const MainPage = () => {
  const [showCreatePost, setShowCreatePost] = useState(false);

  return (
    <div className="home-page">
      <Navbar showSearch={true} />
      
      <div className="home-content">
        {/* Home Header */}
        <div className="home-header">
          <div className="header-info">
            <h1 className="home-title">Home</h1>
            <p className="home-subtitle">Welcome back! Here's what's happening in your network.</p>
          </div>
        </div>

        {/* Main Content Area */}
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

      {/* Create Post Modal */}
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

export default MainPage;
