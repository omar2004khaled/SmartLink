import React from 'react';
import { ExternalLink, Edit2 } from 'lucide-react';
import './ActionButtons.css';

export default function ActionButtons({ 
  onFollow, 
  onVisitWebsite,
  isFollowing,
  isOwner = false,
}) {
  return (
    <div className="action-buttons">
      {!isOwner &&onFollow && (
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
