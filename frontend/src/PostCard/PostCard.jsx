import React from 'react';
import UserHeader from './UserHeader';
import Content from './Content';
import Attachment from './Attachment';
import Footer from './Footer';
import './PostCard.css';

export default function PostCard({ Posts = [] }) {
  // Expect Posts to be an array of post objects. Example shape:
  // { id, username, time, content, attachment }
  if (!Array.isArray(Posts)) {
    // If a single post object is passed, wrap it for convenience
    Posts = [Posts];
  }
  console.log('Rendering PostCard with posts:', Posts);

  return (
    <>
      {Posts.map((post, idx) => {
        const key = post.id ?? idx;
        return (
          <div className="post-card" key={key}>
            <UserHeader username={post.username ?? 'User'} time={post.time ?? 'just now'} />
            <Content content={post.content ?? ''} />
            {post.attachment && (
              <Attachment attachment={post.attachment} onRemove={post.onRemove ?? (() => {})} />
            )}
            <Footer />
          </div>
        );
      })}
    </>
  );
}