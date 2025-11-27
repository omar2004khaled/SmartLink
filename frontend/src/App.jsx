import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import CreatePost from './PostComposotion/PostComposotion'
import PostCard from './PostCard/PostCard'
import { GetPosts } from './FetchData/FetchData';

import './App.css'

function App() {
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(false)
  const [currentPage, setCurrentPage] = useState(0)
  const pageSize = 5;

  // Calculate if there are more posts based on current state
  const hasMore = posts.length === (currentPage + 1) * pageSize && posts.length > 0;

  // Initial load
  useEffect(() => {
    loadInitialPosts();
  }, []);

  const loadInitialPosts = async () => {
    try {
      setLoading(true);
      const postsData = await GetPosts(0, pageSize, 'PostId', false);
      
      if (postsData !== null) {
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
      
      // If service returns null, stop loading more
      if (postsData === null) {
        return;
      }
      
      if (postsData && postsData.length > 0) {
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
      id: post.postId,
      username: `User${post.userId}`,
      time: formatTime(post.createdAt),
      content: post.content,
      attachment: post.attachments && post.attachments.length > 0 ? post.attachments[0] : null
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
    <BrowserRouter>
      <Routes>
        <Route path="/PostComposation" element={<CreatePost />} />
        <Route path='/' element={
          <div>
            <PostCard Posts={posts} />
            
            {/* Show load more button only if we have posts and might have more */}
            {posts.length > 0 && posts.length % pageSize === 0 && (
              <div className="load-more-container" style={{  alignItems:'center' , marginTop:'20px', marginBottom:'20px', display:'flex', justifyContent:'center' , paddingBottom:'15px' }}>
                <button 
                  className="load-more-btn"
                  onClick={loadMorePosts}
                  disabled={loading}
                >
                  {loading ? 'Loading...' : 'Load More Posts'}
                </button>
              </div>
            )}
            
            {/* Show message when no posts at all */}
            {posts.length === 0 && !loading && (
              <div className="no-posts">
                No posts available. Create the first post!
              </div>
            )}
          </div>
        } />   
      </Routes>
    </BrowserRouter>
  )
}

export default App;