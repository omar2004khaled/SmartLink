import React, { useState, useEffect } from 'react';
import { Trash2, Calendar, User, AlertCircle, ChevronLeft, ChevronRight, FileText, Flag, CheckCircle, XCircle, AlertTriangle, Ban } from 'lucide-react';
import { API_BASE_URL } from '../config';

const AdminContentManagement = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [notification, setNotification] = useState({ message: '', type: '' });
  const [activeTab, setActiveTab] = useState('all');
  const [reportedPosts, setReportedPosts] = useState([]);
  const [selectedReport, setSelectedReport] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [reportStatus, setReportStatus] = useState('');
  const [reportCategory, setReportCategory] = useState('');
  const [reportStats, setReportStats] = useState({
    totalReports: 0,
    pendingReports: 0,
    resolvedReports: 0,
    highPriorityReports: 0
  });

  useEffect(() => {
    if (activeTab === 'all') {
      fetchPosts(currentPage, 10, searchTerm);
    } else if (activeTab === 'reported') {
      fetchReportedPosts(currentPage, reportStatus, reportCategory, searchTerm);
      fetchReportStats();
    }
  }, [currentPage, activeTab, searchTerm, reportStatus, reportCategory]);

  const showNotification = (message, type) => {
    setNotification({ message, type });
    setTimeout(() => setNotification({ message: '', type: '' }), 3000);
  };

  const fetchPosts = async (page = 0, size = 10, search = '') => {
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

      const response = await fetch(`${API_BASE_URL}/admin/posts?page=${page}&size=${size}&search=${encodeURIComponent(search)}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        //console.log('Posts data received:', data);
        if (data.posts && data.posts.length > 0) {
          //console.log('First post structure:', data.posts[0]);
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

  const fetchReportedPosts = async (page = 0, status = '', category = '', search = '') => {
    try {
      setLoading(true);
      const token = localStorage.getItem("authToken");

      if (!token) {
        showNotification('Authentication required. Please log in.', 'error');
        setLoading(false);
        return;
      }

      const params = new URLSearchParams({
        page: page.toString(),
        size: '10',
        status,
        ...(category && { category }),
        search
      });

      const response = await fetch(`${API_BASE_URL}/admin/reports?${params}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        //console.log('Reported posts data received:', data);
        if (data.content && data.content.length > 0) {
          //console.log('First report structure:', data.content[0]);
          //console.log('First report post:', data.content[0].post);
        }
        setReportedPosts(data.content || []);
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
        showNotification(errorText || 'Failed to fetch reported posts', 'error');
      }
    } catch (error) {
      if (error.name === 'TypeError' && error.message.includes('Failed to fetch')) {
        showNotification('Cannot connect to server. Please check if the backend is running.', 'error');
      } else {
        showNotification('Error fetching reported posts', 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchReportStats = async () => {
    try {
      const token = localStorage.getItem("authToken");

      if (!token) {
        return;
      }

      const response = await fetch(`${API_BASE_URL}/admin/reports/stats`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setReportStats(data);
      }
    } catch (error) {
      console.error('Error fetching report stats:', error);
    }
  };

  const resolveReport = async (reportId, action, adminNote = '') => {
    try {
      const token = localStorage.getItem("authToken");

      if (!token) {
        showNotification('Authentication required. Please log in.', 'error');
        return;
      }

      const response = await fetch(`${API_BASE_URL}/admin/reports/${reportId}/resolve`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ action, adminNote })
      });

      if (response.ok) {
        showNotification('Report resolved successfully!', 'success');
        fetchReportedPosts(currentPage);
        fetchReportStats();
      } else if (response.status === 401) {
        showNotification('Authentication failed. Please log in again.', 'error');
        localStorage.removeItem('authToken');
      } else if (response.status === 403) {
        showNotification('Access denied. Admin privileges required.', 'error');
      } else {
        const errorText = await response.text();
        showNotification(errorText || 'Failed to resolve report', 'error');
      }
    } catch (error) {
      if (error.name === 'TypeError' && error.message.includes('Failed to fetch')) {
        showNotification('Cannot connect to server. Please check if the backend is running.', 'error');
      } else {
        showNotification('Error resolving report', 'error');
      }
    }
  };

  const deleteUser = async (userId, userName) => {
    if (!window.confirm(`Are you sure you want to delete user "${userName}"? This action cannot be undone.`)) return;

    try {
      const token = localStorage.getItem("authToken");

      if (!token) {
        showNotification('Authentication required. Please log in.', 'error');
        return;
      }

      const response = await fetch(`${API_BASE_URL}/admin/users/${userId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        showNotification(`User "${userName}" deleted successfully!`, 'success');
        // Refresh the reports to reflect the change
        fetchReportedPosts(currentPage);
      } else {
        const errorText = await response.text();
        showNotification(errorText || 'Failed to delete user', 'error');
      }
    } catch (error) {
      showNotification('Error deleting user', 'error');
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
          {activeTab === 'all' ? `Total Posts: ${totalElements}` : `Total Reports: ${totalElements}`}
        </div>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          <button
            onClick={() => setActiveTab('all')}
            className={`py-2 px-1 border-b-2 font-medium text-sm ${activeTab === 'all'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
          >
            All Posts
          </button>
          <button
            onClick={() => setActiveTab('reported')}
            className={`py-2 px-1 border-b-2 font-medium text-sm ${activeTab === 'reported'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
          >
            Reported Posts
          </button>
        </nav>
      </div>

      {/* Statistics Card */}
      {activeTab === 'reported' && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="bg-white p-4 rounded-lg shadow-md">
            <div className="flex items-center">
              <Flag className="h-8 w-8 text-red-500" />
              <div className="ml-3">
                <p className="text-sm font-medium text-gray-500">Total Reports</p>
                <p className="text-2xl font-semibold text-gray-900">{reportStats.totalReports}</p>
              </div>
            </div>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-md">
            <div className="flex items-center">
              <AlertTriangle className="h-8 w-8 text-yellow-500" />
              <div className="ml-3">
                <p className="text-sm font-medium text-gray-500">Pending</p>
                <p className="text-2xl font-semibold text-gray-900">{reportStats.pendingReports}</p>
              </div>
            </div>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-md">
            <div className="flex items-center">
              <CheckCircle className="h-8 w-8 text-green-500" />
              <div className="ml-3">
                <p className="text-sm font-medium text-gray-500">Resolved</p>
                <p className="text-2xl font-semibold text-gray-900">{reportStats.resolvedReports}</p>
              </div>
            </div>
          </div>
          <div className="bg-white p-4 rounded-lg shadow-md">
            <div className="flex items-center">
              <Ban className="h-8 w-8 text-red-600" />
              <div className="ml-3">
                <p className="text-sm font-medium text-gray-500">High Priority</p>
                <p className="text-2xl font-semibold text-gray-900">{reportStats.highPriorityReports}</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'all' && (
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
      )}

      {/* Search and Filters */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex flex-col md:flex-row gap-4">
          {/* Search */}
          <div className="flex-1">
            <div className="relative">
              <span className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                <FileText size={20} />
              </span>
              <input
                type="text"
                placeholder={activeTab === 'all' ? "Search posts by content..." : "Search reports by description or ID..."}
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setCurrentPage(0);
                }}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
          </div>

          {/* Report Specific Filters */}
          {activeTab === 'reported' && (
            <>
              <div className="md:w-48">
                <select
                  value={reportStatus}
                  onChange={(e) => {
                    setReportStatus(e.target.value);
                    setCurrentPage(0);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">All Statuses</option>
                  <option value="PENDING">Pending</option>
                  <option value="RESOLVED">Resolved</option>
                </select>
              </div>
              <div className="md:w-48">
                <select
                  value={reportCategory}
                  onChange={(e) => {
                    setReportCategory(e.target.value);
                    setCurrentPage(0);
                  }}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">All Categories</option>
                  <option value="SPAM">Spam</option>
                  <option value="HATE_SPEECH">Hate Speech</option>
                  <option value="SEXUAL_HARASSMENT">Harassment</option>
                  <option value="VIOLENCE">Violence</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Notification */}
      {notification.message && (
        <div className={`p-4 rounded-lg flex items-center gap-2 ${notification.type === 'success'
            ? 'bg-green-50 border border-green-200 text-green-700'
            : 'bg-red-50 border border-red-200 text-red-700'
          }`}>
          <AlertCircle size={16} />
          {notification.message}
        </div>
      )}

      {/* Content Table */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          {activeTab === 'all' ? (
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
                      <td className="px-6 py-4 text-sm text-gray-900 max-w-xs truncate">
                        {post.content}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {post.authorName || post.userEmail || 'Unknown'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {new Date(post.createdAt).toLocaleDateString()}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button
                          onClick={() => deletePost(post.postId)}
                          className="text-red-600 hover:text-red-900 flex items-center gap-1"
                        >
                          <Trash2 size={16} />
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          ) : (
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Report ID
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-2/5">
                    Post Content
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Post Author
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Category
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Reporter
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {loading ? (
                  <tr>
                    <td colSpan="7" className="px-6 py-4 text-center text-gray-500">
                      Loading reports...
                    </td>
                  </tr>
                ) : reportedPosts.length === 0 ? (
                  <tr>
                    <td colSpan="7" className="px-6 py-4 text-center text-gray-500">
                      No reported posts found
                    </td>
                  </tr>
                ) : (
                  reportedPosts.map((report) => (
                    <tr key={report.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {report.id}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900 min-w-0 w-2/5">
                        <div
                          className="max-h-48 overflow-y-auto border rounded-lg p-4 bg-white shadow-sm border-gray-200 cursor-pointer hover:shadow-md transition-shadow"
                          onClick={() => setSelectedReport(report)}
                          title="Click to view full details"
                        >
                          <div className="whitespace-pre-wrap break-words text-gray-800 leading-relaxed">
                            {report.post?.content || (
                              <span className="text-red-500 italic">Post content not available - post may have been deleted</span>
                            )}
                          </div>
                          {report.post?.content && (
                            <div className="mt-2 pt-2 border-t border-gray-100">
                              <span className="text-xs text-gray-500">
                                Posted on: {report.post?.createdAt ? new Date(report.post.createdAt).toLocaleString() : 'Unknown'}
                              </span>
                            </div>
                          )}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <div className="flex flex-col">
                          <span className="font-medium">{report.post?.author?.name || 'Unknown'}</span>
                          <span className="text-xs text-gray-400">{report.post?.author?.email}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <span className={`px-2 py-1 text-xs rounded-full ${report.category === 'HATE_SPEECH' ? 'bg-red-100 text-red-800' :
                            report.category === 'SEXUAL_HARASSMENT' ? 'bg-pink-100 text-pink-800' :
                              report.category === 'VIOLENCE' ? 'bg-orange-100 text-orange-800' :
                                report.category === 'SPAM' ? 'bg-yellow-100 text-yellow-800' :
                                  'bg-gray-100 text-gray-800'
                          }`}>
                          {report.category.replace('_', ' ')}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {report.reporter?.name || 'Anonymous'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <span className={`px-2 py-1 text-xs rounded-full ${report.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                            report.status === 'RESOLVED' ? 'bg-green-100 text-green-800' :
                              'bg-gray-100 text-gray-800'
                          }`}>
                          {report.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                        <button
                          onClick={() => {
                            //console.log('Opening modal for report:', report);
                            //console.log('Report status:', report.status);
                            setSelectedReport(report);
                          }}
                          className="text-blue-600 hover:text-blue-900 flex items-center gap-1"
                        >
                          <FileText size={16} />
                          View
                        </button>
                        {report.status === 'PENDING' && (
                          <>
                            <button
                              onClick={() => resolveReport(report.id, 'APPROVE_POST')}
                              className="text-green-600 hover:text-green-900 flex items-center gap-1"
                            >
                              <CheckCircle size={16} />
                              Approve
                            </button>
                            <button
                              onClick={() => resolveReport(report.id, 'WARN_USER')}
                              className="text-yellow-600 hover:text-yellow-900 flex items-center gap-1"
                            >
                              <AlertTriangle size={16} />
                              Warn User
                            </button>
                            <button
                              onClick={() => resolveReport(report.id, 'REMOVE_POST')}
                              className="text-red-600 hover:text-red-900 flex items-center gap-1"
                            >
                              <XCircle size={16} />
                              Remove
                            </button>
                          </>
                        )}
                        {report.post?.author && (
                          <button
                            onClick={() => deleteUser(report.post.author.id, report.post.author.name)}
                            className="text-red-700 hover:text-red-900 flex items-center gap-1 ml-2"
                            title={`Delete user: ${report.post.author.name}`}
                          >
                            <User size={16} />
                            Delete User
                          </button>
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          )}
        </div>
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
                      className={`relative inline-flex items-center px-4 py-2 border text-sm font-medium ${currentPage === pageNum
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

      {/* Post Content Modal */}
      {selectedReport && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg max-w-4xl w-full mx-4 max-h-[90vh] overflow-hidden">
            <div className="flex justify-between items-center p-6 border-b">
              <h3 className="text-lg font-semibold text-gray-900">Review Reported Post</h3>
              <button
                onClick={() => setSelectedReport(null)}
                className="text-gray-400 hover:text-gray-600"
              >
                <XCircle size={24} />
              </button>
            </div>
            <div className="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Post Content</h4>
                  <div className="bg-gray-50 p-4 rounded-lg border min-h-32">
                    <div className="whitespace-pre-wrap break-words text-gray-800">
                      {selectedReport.post?.content || 'Post content not available'}
                    </div>
                    {selectedReport.post?.content === 'This post has been deleted or is no longer available.' && (
                      <div className="mt-2 p-2 bg-red-50 border border-red-200 rounded text-red-700 text-sm">
                        <strong>Note:</strong> This post has been deleted or is no longer available.
                      </div>
                    )}
                  </div>
                  <div className="mt-4 text-sm text-gray-600">
                    <p><strong>Posted by:</strong> {selectedReport.post?.author?.name || 'Unknown'}</p>
                    <p><strong>Posted on:</strong> {selectedReport.post?.createdAt ? new Date(selectedReport.post.createdAt).toLocaleString() : 'Unknown'}</p>
                  </div>
                </div>
                <div>
                  <h4 className="font-semibold text-gray-900 mb-2">Report Details</h4>
                  <div className="space-y-3">
                    <div>
                      <span className="text-sm font-medium text-gray-500">Report ID:</span>
                      <span className="ml-2 text-sm text-gray-900">{selectedReport.id}</span>
                    </div>
                    <div>
                      <span className="text-sm font-medium text-gray-500">Category:</span>
                      <span className={`ml-2 px-2 py-1 text-xs rounded-full ${selectedReport.category === 'HATE_SPEECH' ? 'bg-red-100 text-red-800' :
                          selectedReport.category === 'SEXUAL_HARASSMENT' ? 'bg-pink-100 text-pink-800' :
                            selectedReport.category === 'VIOLENCE' ? 'bg-orange-100 text-orange-800' :
                              selectedReport.category === 'SPAM' ? 'bg-yellow-100 text-yellow-800' :
                                'bg-gray-100 text-gray-800'
                        }`}>
                        {selectedReport.category.replace('_', ' ')}
                      </span>
                    </div>
                    <div>
                      <span className="text-sm font-medium text-gray-500">Reported by:</span>
                      <span className="ml-2 text-sm text-gray-900">{selectedReport.reporter?.name || 'Anonymous'}</span>
                    </div>
                    <div>
                      <span className="text-sm font-medium text-gray-500">Report Description:</span>
                      <div className="ml-2 mt-1 p-2 bg-gray-50 rounded text-sm text-gray-800">
                        {selectedReport.description || 'No description provided'}
                      </div>
                    </div>
                    <div>
                      <span className="text-sm font-medium text-gray-500">Status:</span>
                      <span className={`ml-2 px-2 py-1 text-xs rounded-full ${selectedReport.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                          selectedReport.status === 'RESOLVED' ? 'bg-green-100 text-green-800' :
                            'bg-gray-100 text-gray-800'
                        }`}>
                        {selectedReport.status}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              {selectedReport.status === 'PENDING' && (
                <div className="mt-6 pt-6 border-t">
                  <h4 className="font-semibold text-gray-900 mb-4">Actions</h4>
                  <div className="flex space-x-3">
                    <button
                      onClick={() => {
                        //console.log('Resolving report:', selectedReport.id, 'with action: APPROVE_POST');
                        resolveReport(selectedReport.id, 'APPROVE_POST');
                        setSelectedReport(null);
                      }}
                      className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 flex items-center gap-2"
                    >
                      <CheckCircle size={16} />
                      Approve Post
                    </button>
                    <button
                      onClick={() => {
                        //console.log('Resolving report:', selectedReport.id, 'with action: WARN_USER');
                        resolveReport(selectedReport.id, 'WARN_USER');
                        setSelectedReport(null);
                      }}
                      className="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 flex items-center gap-2"
                    >
                      <AlertTriangle size={16} />
                      Warn User
                    </button>
                    <button
                      onClick={() => {
                        //console.log('Resolving report:', selectedReport.id, 'with action: REMOVE_POST');
                        resolveReport(selectedReport.id, 'REMOVE_POST');
                        setSelectedReport(null);
                      }}
                      className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 flex items-center gap-2"
                    >
                      <XCircle size={16} />
                      Remove Post
                    </button>
                    {selectedReport.post?.author && selectedReport.post?.author?.id && (
                      <button
                        onClick={() => {
                          //console.log('Deleting user:', selectedReport.post.author.id);
                          deleteUser(selectedReport.post.author.id, selectedReport.post.author.name);
                          setSelectedReport(null);
                        }}
                        className="px-4 py-2 bg-red-700 text-white rounded-lg hover:bg-red-800 flex items-center gap-2"
                      >
                        <User size={16} />
                        Delete User
                      </button>
                    )}
                  </div>
                </div>
              )}

              {selectedReport.status === 'RESOLVED' && (
                <div className="mt-6 pt-6 border-t">
                  <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                    <div className="flex items-center gap-2 text-green-700">
                      <CheckCircle size={20} />
                      <span className="font-semibold">This report has been resolved</span>
                    </div>
                    <p className="text-green-600 text-sm mt-1">
                      The reported content has already been reviewed and appropriate action has been taken.
                    </p>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminContentManagement;