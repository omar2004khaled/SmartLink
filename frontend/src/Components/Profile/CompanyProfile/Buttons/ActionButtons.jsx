import React from 'react';
import { ExternalLink, Edit2, MessageCircle } from 'lucide-react';
import './ActionButtons.css';

export default function ActionButtons({ 
  onFollow, 
  onVisitWebsite,
  onMessage,
  isFollowing,
  isOwner = false,
}) {
  return (
    <div className="action-buttons">
      {!isOwner && onMessage && (
        <button onClick={onMessage} className="btn-message">
          <MessageCircle className="icon" />
          Message
        </button>
      )}
      
      {!isOwner && onFollow && (
        <button onClick={onFollow} className={`btn-follow ${isFollowing ? 'following' : ''}`}>
              {isFollowing ? 'Following' : 'Follow'}
            </button>

      )}
      {!isOwner&&onVisitWebsite && (
            <button onClick={onVisitWebsite} className="btn-website">
              Visit website
              <ExternalLink className="icon" />
            </button>
          )}
    </div>
  );
}
