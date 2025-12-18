import React, { useState, useEffect } from 'react';
import { Trash2, Calendar, User, AlertCircle, ChevronLeft, ChevronRight, FileText } from 'lucide-react';
import { API_BASE_URL } from '../config';

const AdminContentManagement = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [notification, setNotification] = useState({ message: '', type: '' });

  useEffect(() => {
    fetchPosts();
  }, [currentPage]);

  const showNotification = (message, type) => {
    setNotification({ message, type });
    setTimeout(() => setNotification({ message: '', type: '' }), 3000);
  };

  const fetchPosts = async (page = 0, size = 10) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("authToken");
      
      if (!token) {
        showNotification('Authentication required. Please log in.', 'error');
        setLoading(false);
        return;
      }

      // Check if token is expired
      try {
        const tokenPayload = JSON.parse(atob(token.split('.')[1]));
        if (tokenPayload.exp * 1000 < Date.now()) {
          showNotification('Session expired. Please log in again.', 'error');
          localStorage.removeItem('authToken');
          setLoading(false);
          return;
        }
      } catch (e) {
        showNotification('Invalid token. Please log in again.', 'error');
        localStorage.removeItem('authToken');
        setLoading(false);
        return;
      }

      const response = await fetch(`${API_BASE_URL}/admin/posts?page=${page}&size=${size}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        console.log('Posts data received:', data);
        if (data.posts && data.posts.length > 0) {
          console.log('First post structure:', data.posts[0]);
        }
        setPosts(data.posts || []);
        setTotalPages(data.totalPages || 0);
        setCurrentPage(data.currentPage || 0);
        setTotalElements(data.totalElements || 0);
      } else if (response.status === 401) {
        showNotification('Authentication failed. Please log in again.', 'error');
        localStorage.removeItem('authToken');
      } else if (response.status === 403) {
        showNotification('Access denied. Admin privileges required.', 'error');
      } else {
        const errorText = await response.text();
        showNotification(errorText || 'Failed to fetch posts', 'error');
      }
    } catch (error) {
      if (error.name === 'TypeError' && error.message.includes('Failed to fetch')) {
        showNotification('Cannot connect to server. Please check if the backend is running.', 'error');
      } else {
        showNotification('Error fetching posts', 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  const deletePost = async (postId) => {
    if (!window.confirm('Are you sure you want to delete this post?')) return;

    try {
      const token = localStorage.getItem('authToken');
      
      if (!token) {
        showNotification('Please log in to perform this action', 'error');
        return;
      }
      
      const response = await fetch(`${API_BASE_URL}/admin/posts/${postId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        showNotification('Post deleted successfully!', 'success');
        fetchPosts(currentPage, 10);
      } else {
        const errorText = await response.text();
        showNotification(errorText || 'Failed to delete post', 'error');
      }
    } catch (error) {
      showNotification('Error deleting post', 'error');
    }
  };

  if (loading) {
    return <div className="flex justify-center items-center h-64">Loading posts...</div>;
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Content Management</h2>
        <div className="text-sm text-gray-500">
          Total Posts: {totalElements}
        </div>
      </div>

      {/* Statistics Card */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-4 rounded-lg shadow-md">
          <div className="flex items-center">
            <FileText className="h-8 w-8 text-blue-500" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Total Posts</p>
              <p className="text-2xl font-semibold text-gray-900">{totalElements}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Notification */}
      {notification.message && (
        <div className={`p-4 rounded-lg flex items-center gap-2 ${
          notification.type === 'success' 
            ? 'bg-green-50 border border-green-200 text-green-700' 
            : 'bg-red-50 border border-red-200 text-red-700'
        }`}>
          <AlertCircle size={16} />
          {notification.message}
        </div>
      )}

      {/* Posts Table */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Post ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Content
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Author
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Created At
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                <tr>
                  <td colSpan="5" className="px-6 py-4 text-center text-gray-500">
                    Loading posts...
                  </td>
                </tr>
              ) : posts.length === 0 ? (
                <tr>
                  <td colSpan="5" className="px-6 py-4 text-center text-gray-500">
                    No posts found
                  </td>
                </tr>
              ) : (
                posts.map((post) => (
                  <tr key={post.postId} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {post.postId}
                    </td>
                    <td className="px-6 py-4">
                      <div className="text-sm text-gray-900 max-w-xs truncate">
                        {post.content || 'No content'}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <User size={16} className="text-gray-400 mr-2" />
                        <span className="text-sm text-gray-900">{post.userEmail || `User ${post.userId || post.UserId}`}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <Calendar size={16} className="text-gray-400 mr-2" />
                        <span className="text-sm text-gray-900">
                          {(post.createdAt || post.CreatedAt) ? new Date(post.createdAt || post.CreatedAt).toLocaleDateString() : 'N/A'}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => deletePost(post.postId)}
                        className="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50"
                        title="Delete Post"
                      >
                        <Trash2 size={16} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
            <div className="flex-1 flex justify-between sm:hidden">
              <button
                onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                disabled={currentPage === 0}
                className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Previous
              </button>
              <button
                onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                disabled={currentPage === totalPages - 1}
                className="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Next
              </button>
            </div>
            <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
              <div>
                <p className="text-sm text-gray-700">
                  Showing <span className="font-medium">{currentPage * 10 + 1}</span> to{' '}
                  <span className="font-medium">
                    {Math.min((currentPage + 1) * 10, totalElements)}
                  </span>{' '}
                  of <span className="font-medium">{totalElements}</span> results
                </p>
              </div>
              <div>
                <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
                  <button
                    onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                    disabled={currentPage === 0}
                    className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <ChevronLeft size={20} />
                  </button>
                  
                  {/* Page Numbers */}
                  {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                    const pageNum = Math.max(0, Math.min(totalPages - 5, currentPage - 2)) + i;
                    return (
                      <button
                        key={pageNum}
                        onClick={() => setCurrentPage(pageNum)}
                        className={`relative inline-flex items-center px-4 py-2 border text-sm font-medium ${
                          currentPage === pageNum
                            ? 'z-10 bg-blue-50 border-blue-500 text-blue-600'
                            : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'
                        }`}
                      >
                        {pageNum + 1}
                      </button>
                    );
                  })}

                  <button
                    onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                    disabled={currentPage === totalPages - 1}
                    className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <ChevronRight size={20} />
                  </button>
                </nav>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminContentManagement;