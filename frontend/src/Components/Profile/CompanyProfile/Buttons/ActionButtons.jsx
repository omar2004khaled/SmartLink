import React from 'react';
import { ExternalLink, MoreHorizontal } from 'lucide-react';
import './ActionButtons.css';

export default function ActionButtons({ onFollow, onVisitWebsite,isFollowing }) {
  return (
    <div className="action-buttons">
      <button onClick={onFollow} className={`btn-follow ${isFollowing ? 'following' : ''}`}>
        {isFollowing ? 'Following' : '+ Follow'}
      </button>
      <button onClick={onVisitWebsite} className="btn-website">
        Visit website
        <ExternalLink className="icon" />
      </button>
    </div>
  );
}