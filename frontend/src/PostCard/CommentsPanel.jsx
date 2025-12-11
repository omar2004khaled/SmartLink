import React, { useState, useEffect } from 'react';
import './PostCard.css';

export default function CommentsPanel({ comments = [], onClose }) {
  const INITIAL_COUNT = 7;
  const LOAD_COUNT = 7;
  const [visibleCount, setVisibleCount] = useState(INITIAL_COUNT);

  useEffect(() => {
    setVisibleCount(INITIAL_COUNT);
  }, [comments]);

  const handleLoadMore = () => {
    setVisibleCount((v) => Math.min(comments.length, v + LOAD_COUNT));
  };

  const handleHide = () => setVisibleCount(0);

  const visibleComments = comments.slice(0, visibleCount);

  return (
    <div className="comments-modal-overlay" onClick={onClose}>
      <div className="comments-modal" onClick={(e) => e.stopPropagation()}>
        <div className="comments-modal-header">
          <strong>Comments</strong>
          <div className="comments-controls">
            <button className="comments-hide-btn" onClick={handleHide}>Hide</button>
            <button className="comments-modal-close" onClick={onClose} aria-label="Close">&times;</button>
          </div>
        </div>

        <div className="comments-modal-body">
          {visibleCount === 0 && (
            <div className="no-comments">Comments hidden</div>
          )}

          {visibleCount > 0 && visibleComments.length === 0 && (
            <div className="no-comments">No comments yet</div>
          )}

          {visibleComments.map((c, i) => (
            <div key={c.commentId ?? i} className="comment-item">
              <img
                src={c.avatar || '/src/PostCard/avatar.png'}
                alt="avatar"
                className="comment-avatar"
              />
              <div className="comment-body">
                <div className="comment-author">{c.author || 'Anonymous'}</div>
                <div className="comment-text">{c.text}</div>
                {c.attachment && (
                  <div style={{ marginTop: 8 }}>
                    <img src={c.attachment} alt="attachment" style={{ maxWidth: '100%', borderRadius: 8 }} />
                  </div>
                )}
                {c.time && <div className="comment-time">{c.time}</div>}
              </div>
            </div>
          ))}

        </div>

        <div className="comments-modal-footer">
          <div className="comments-footer-left">
            <span className="comments-count">Showing {Math.min(visibleCount, comments.length)} of {comments.length}</span>
          </div>
          <div className="comments-footer-actions">
            {visibleCount < comments.length && visibleCount !== 0 && (
              <button className="load-more-btn" onClick={handleLoadMore}>Load more</button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
