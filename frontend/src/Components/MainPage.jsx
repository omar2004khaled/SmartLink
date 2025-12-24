import React, { useState, useRef } from 'react';
import { Plus, Shield } from 'lucide-react';
import { Link } from 'react-router-dom';
import Navbar from './Navbar';
import Posts from './Posts';
import PostComposotion from '../PostComposotion/PostComposotion';
import './MainPage.css';

const MainPage = () => {
  const [showCreatePost, setShowCreatePost] = useState(false);
  const postsRef = useRef(null);

  const handlePostCreated = () => {
    ////console.log('Post created! Refreshing posts...');
    // Refresh posts to show the new post
    if (postsRef.current) {
      postsRef.current.refreshPosts();
    } else {
      console.error('postsRef.current is null!');
    }
    // Close the modal
    setShowCreatePost(false);
  };

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
            <div className="header-actions">
              <button className="quick-post-btn" onClick={() => setShowCreatePost(true)}>
                <Plus size={16} />
                Share something
              </button>
            </div>
          </div>
          <Posts ref={postsRef} />
        </div>
      </div>

      {/* Create Post Modal */}
      {showCreatePost && (
        <div className="modal-overlay" onClick={() => setShowCreatePost(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <PostComposotion onPostCreated={handlePostCreated} />
          </div>
        </div>
      )}
    </div>
  );
};

export default MainPage;
