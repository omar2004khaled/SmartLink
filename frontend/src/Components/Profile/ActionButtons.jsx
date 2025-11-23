import React from 'react';
import { ExternalLink, MoreHorizontal } from 'lucide-react';

export default function ActionButtons({ onFollow, onVisitWebsite,isFollowing }) {
  return (
    <div>
      <button onClick={onFollow}>
        {isFollowing ? 'Following' : '+ Follow'}
      </button>
      <button onClick={onVisitWebsite}>
        Visit website
        <ExternalLink />
      </button>
    </div>
  );
}