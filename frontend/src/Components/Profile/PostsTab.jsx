import React from 'react';

export default function PostsTab({ posts }) {
  return (
    <div>
      {posts && posts.length > 0 ? (
        posts.map((post) => (
          <div key={post.id}>
            {/* view posts */}
          </div>
        ))
      ) : (
        <p>No posts</p>
      )}
    </div>
  );
}