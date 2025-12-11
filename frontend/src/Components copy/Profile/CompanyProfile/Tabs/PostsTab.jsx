import React from 'react';
import './PostsTab.css';

export default function PostsTab({ posts }) {
  return (
    <div className="posts-tab">
      {posts && posts.length > 0 ? (
        posts.map((post) => (
          <div key={post.id} className="post-item">
            {/* view posts */}
          </div>
        ))
      ) : (
        <p className="no-posts">No posts</p>
      )}
    </div>
  );
}