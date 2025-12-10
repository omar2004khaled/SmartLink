import React, { useState } from 'react';
import { Plus } from 'lucide-react';
import Navbar from './Navbar';
import Posts from './Posts';
import PostComposotion from '../PostComposotion/PostComposotion';
import './MainPage.css';

const MainPage = () => {
  const [showCreatePost, setShowCreatePost] = useState(false);

  return (
    <div className="main-page">
      <Navbar showSearch={true} />

      {/* Create Post Bar */}
      <div className="create-post-bar">
        <div className="create-post-container">
          <button className="create-post-btn" onClick={() => setShowCreatePost(true)}>
            <Plus size={20} />
            <span>What's on your mind?</span>
          </button>
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

      {/* Main Content - Posts */}
      <Posts />
    </div>
  );
};

export default MainPage;
