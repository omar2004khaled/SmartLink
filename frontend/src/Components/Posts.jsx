import React, { useState, useEffect } from 'react';
import PostCard from '../PostCard/PostCard';
import { GetPosts } from '../FetchData/FetchData';

export default function Posts() {
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(false)
  const [currentPage, setCurrentPage] = useState(0)
  const pageSize = 5;

  useEffect(() => {
    loadInitialPosts();
  }, []);

  const loadInitialPosts = async () => {
    try {
      setLoading(true);
      const postsData = await GetPosts(0, pageSize, 'PostId', false);
      
      if (postsData !== null && Array.isArray(postsData)) {
        const transformedPosts = transformPosts(postsData);
        setPosts(transformedPosts);
        setCurrentPage(0);
      }
    } catch (error) {
      console.error('Error fetching posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadMorePosts = async () => {
    if (loading) return;
    
    try {
      setLoading(true);
      const nextPage = currentPage + 1;
      const postsData = await GetPosts(nextPage, pageSize, 'PostId', false);
      
      if (postsData === null) {
        return;
      }
      
      if (postsData && Array.isArray(postsData) && postsData.length > 0) {
        const transformedPosts = transformPosts(postsData);
        setPosts(prevPosts => [...prevPosts, ...transformedPosts]);
        setCurrentPage(nextPage);
      }
    } catch (error) {
      console.error('Error fetching more posts:', error);
    } finally {
      setLoading(false);
    }
  };

  const transformPosts = (postsData) => {
    return postsData.map(post => ({
      id: post.id,
      userId: post.userId,
      username: post.userName || `User${post.userId}`,
      time: formatTime(post.createdAt),
      content: post.content,
      attachments: post.attachments || []
    }));
  };

  const formatTime = (timestamp) => {
    if (!timestamp) return 'just now';
    
    const postTime = new Date(timestamp);
    const now = new Date();
    const diffInHours = (now - postTime) / (1000 * 60 * 60);
    
    if (diffInHours < 1) return 'just now';
    if (diffInHours < 24) return `${Math.floor(diffInHours)} hours ago`;
    return `${Math.floor(diffInHours / 24)} days ago`;
  };

  return (
    <div>
      {posts.map(p => (
        <PostCard key={p.id} post={p} />
      ))}

      {posts.length > 0 && posts.length % pageSize === 0 && (
        <div className="load-more-container" style={{ alignItems:'center', marginTop:'20px', marginBottom:'20px', display:'flex', justifyContent:'center', paddingBottom:'15px' }}>
          <button 
            className="load-more-btn"
            onClick={loadMorePosts}
            disabled={loading}
          >
            {loading ? 'Loading...' : 'Load More Posts'}
          </button>
        </div>
      )}

      {posts.length === 0 && !loading && (
        <div className="no-posts">
          No posts available. Create the first post!
        </div>
      )}
    </div>
  );
}