import React, { useState, useEffect, useRef, forwardRef, useImperativeHandle } from 'react';
import PostCard from '../PostCard/PostCard';
import { GetPosts } from '../FetchData/FetchData';

const Posts = forwardRef((props, ref) => {
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(false)
  const [currentPage, setCurrentPage] = useState(0)
  const [hasMore, setHasMore] = useState(true);
  const pageSize = 5;
  const isFetchingRef = useRef(false);

  useEffect(() => {
    loadInitialPosts();
    return () => {
      isFetchingRef.current = false;
    }
  }, []);

  // Expose refreshPosts to parent via ref
  useImperativeHandle(ref, () => ({
    refreshPosts: loadInitialPosts
  }));

  const loadInitialPosts = async () => {
    if (isFetchingRef.current) return;
    try {
      isFetchingRef.current = true;
      setLoading(true);
      console.log('Refreshing posts...');

      // Reset state for clean refresh
      setPosts([]);
      setCurrentPage(0);
      setHasMore(true);

      const postsData = await GetPosts(0, pageSize, 'PostId', false);

      if (postsData !== null && Array.isArray(postsData)) {
        const transformedPosts = transformPosts(postsData);
        setPosts(transformedPosts);
        setCurrentPage(1);
        setHasMore(postsData.length === pageSize);
        console.log('Posts refreshed successfully:', transformedPosts.length, 'posts loaded');
      }
      else {
        setPosts([]);
        setHasMore(false);
      }
    } catch (error) {
      console.error('Error fetching posts:', error);
      setHasMore(false);
    } finally {
      isFetchingRef.current = false;
      setLoading(false);
    }
  };

  const loadMorePosts = async () => {
    console.log('Loading more posts...');
    if (loading || !hasMore || isFetchingRef.current) return;

    try {
      setLoading(true);
      isFetchingRef.current = true;
      const postsData = await GetPosts(currentPage, pageSize, 'PostId', false);

      if (!postsData || !Array.isArray(postsData) || postsData.length === 0) {
        setHasMore(false);
        return;
      }

      const transformedPosts = transformPosts(postsData);
      const existingPostIds = new Set(posts.map(p => p.id));
      const newUniquePosts = transformedPosts.filter(p => !existingPostIds.has(p.id));
      if (newUniquePosts.length === 0) {
        setHasMore(false);
        console.log('No new unique posts found.');
        return;
      }
      setPosts(prevPosts => [...prevPosts, ...newUniquePosts]);
      setHasMore(postsData.length === pageSize);
      setCurrentPage(prevPage => prevPage + 1);
      console.log(`Loaded ${newUniquePosts.length} new posts.`);
    } catch (error) {
      setHasMore(false);
      console.error('Error fetching more posts:', error);
    } finally {
      isFetchingRef.current = false;
      setLoading(false);
    }
  };

  const transformPosts = (postsData) => {
    // First, deduplicate the incoming data
    const seenIds = new Set();
    const uniquePosts = [];

    for (const post of postsData) {
      const postId = post.id || post.postId;
      if (!seenIds.has(postId)) {
        seenIds.add(postId);
        uniquePosts.push({
          id: postId || post.postId,
          userId: post.userId,
          userType: post.userType, // Preserve userType for routing
          username: post.userName || `User${post.userId}`,
          time: post.createdAt,
          content: post.content,
          // Take only the first attachment
          attachment: post.attachments && post.attachments.length > 0
            ? post.attachments[0]
            : null,
          attachments: post.attachments || []
        });
      }
    }

    console.log(`Transformed ${postsData.length} posts to ${uniquePosts.length} unique posts`);
    return uniquePosts;
  };
  return (
    <div>
      {posts.map(p => (
        <PostCard key={`post-${p.id}-${p.userId}`} post={p} />
      ))}

      {hasMore && (
        <div className="load-more-container" style={{
          alignItems: 'center',
          marginTop: '20px',
          marginBottom: '20px',
          display: 'flex',
          justifyContent: 'center',
          paddingBottom: '15px'
        }}>
          <button
            className="load-more-btn"
            onClick={loadMorePosts}
            disabled={loading || !hasMore}
            style={{
              padding: '10px 20px',
              backgroundColor: loading ? '#ccc' : '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              cursor: loading ? 'not-allowed' : 'pointer'
            }}
          >
            {loading ? 'Loading...' : 'Load More Posts'}
          </button>
        </div>
      )}

      {!hasMore && posts.length > 0 && (
        <div style={{ textAlign: 'center', color: '#666', padding: '20px' }}>
          No more posts to load
        </div>
      )}

      {posts.length === 0 && !loading && (
        <div className="no-posts" style={{ textAlign: 'center', padding: '40px' }}>
          No posts available. Create the first post!
        </div>
      )}
    </div>
  );
});

export default Posts;