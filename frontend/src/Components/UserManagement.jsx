import React, { useState, useEffect } from 'react';
import { Search, ChevronLeft, ChevronRight, Trash2, UserPlus, UserMinus, AlertCircle, Mail, BarChart3, Users, UserCheck, Shield } from 'lucide-react';
import { API_BASE_URL } from '../config';

const UserManagement = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [userTypeFilter, setUserTypeFilter] = useState('');
  const [notification, setNotification] = useState({ message: '', type: '' });
  const [currentUserEmail] = useState(localStorage.getItem('userEmail'));
  const [isSuperAdmin] = useState(currentUserEmail === 'BigBoss@example.com');
  const [stats, setStats] = useState({ totalUsers: 0, activeUsers: 0, adminUsers: 0, companyUsers: 0, jobSeekerUsers: 0 });

  useEffect(() => {
    fetchUsers();
    fetchStats();
  }, [currentPage, searchTerm, userTypeFilter]);

  const fetchStats = async () => {
    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`${API_BASE_URL}/admin/stats`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        setStats(data);
      }
    } catch (error) {
      // Silent fail for stats
    }
  };

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem('authToken');
      const params = new URLSearchParams({
        page: currentPage.toString(),
        size: '10',
        search: searchTerm,
        userType: userTypeFilter
      });

      const response = await fetch(`${API_BASE_URL}/admin/users?${params}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setUsers(data.users);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);
      } else {
        showNotification('Failed to fetch users', 'error');
      }
    } catch (error) {
      showNotification('Error fetching users', 'error');
    } finally {
      setLoading(false);
    }
  };

  const showNotification = (message, type) => {
    setNotification({ message, type });
    setTimeout(() => setNotification({ message: '', type: '' }), 3000);
  };

  const handleSearch = (e) => {
    setSearchTerm(e.target.value);
    setCurrentPage(0);
  };

  const handleFilterChange = (e) => {
    setUserTypeFilter(e.target.value);
    setCurrentPage(0);
  };

  const handleDeleteUser = async (userId, userEmail) => {
    if (!window.confirm(`Are you sure you want to delete user: ${userEmail}?`)) {
      return;
    }

    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`${API_BASE_URL}/admin/users/${userId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        showNotification('User deleted successfully', 'success');
        fetchUsers();
      } else {
        const errorText = await response.text();
        showNotification(errorText || 'Failed to delete user', 'error');
      }
    } catch (error) {
      showNotification('Error deleting user', 'error');
    }
  };

  const handlePromoteUser = async (userId, userEmail) => {
    if (!isSuperAdmin) {
      showNotification('Only super admin can promote users', 'error');
      return;
    }

    if (!window.confirm(`Are you sure you want to promote ${userEmail} to admin?`)) {
      return;
    }

    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`${API_BASE_URL}/admin/promote/${userId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        showNotification('User promoted to admin successfully', 'success');
        fetchUsers();
      } else {
        const errorText = await response.text();
        showNotification(errorText || 'Failed to promote user', 'error');
      }
    } catch (error) {
      showNotification('Error promoting user', 'error');
    }
  };

  const handleDemoteUser = async (userId, userEmail) => {
    if (!isSuperAdmin) {
      showNotification('Only super admin can demote users', 'error');
      return;
    }

    if (!window.confirm(`Are you sure you want to demote ${userEmail} to regular user?`)) {
      return;
    }

    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`${API_BASE_URL}/admin/demote/${userId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        showNotification('User demoted successfully', 'success');
        fetchUsers();
      } else {
        const errorText = await response.text();
        showNotification(errorText || 'Failed to demote user', 'error');
      }
    } catch (error) {
      showNotification('Error demoting user', 'error');
    }
  };

  const handleSendEmail = async (userId, userEmail) => {
    const message = prompt(`Enter message to send to ${userEmail}:`);
    if (!message) return;

    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`${API_BASE_URL}/admin/send-email/${userId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message })
      });

      if (response.ok) {
        showNotification('Email sent successfully', 'success');
      } else {
        const errorText = await response.text();
        showNotification(errorText || 'Failed to send email', 'error');
      }
    } catch (error) {
      showNotification('Error sending email', 'error');
    }
  };

  const getRoleBadgeColor = (role) => {
    return role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-gray-100 text-gray-800';
  };

  const getUserTypeBadgeColor = (userType) => {
    return userType === 'COMPANY' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">User Management</h2>
        <div className="text-sm text-gray-500">
          Total Users: {totalElements}
        </div>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
        <div className="bg-white p-4 rounded-lg shadow-md">
          <div className="flex items-center">
            <Users className="h-8 w-8 text-blue-500" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Total Users</p>
              <p className="text-2xl font-semibold text-gray-900">{stats.totalUsers}</p>
            </div>
          </div>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-md">
          <div className="flex items-center">
            <UserCheck className="h-8 w-8 text-green-500" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Active Users</p>
              <p className="text-2xl font-semibold text-gray-900">{stats.activeUsers}</p>
            </div>
          </div>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-md">
          <div className="flex items-center">
            <Shield className="h-8 w-8 text-purple-500" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Admins</p>
              <p className="text-2xl font-semibold text-gray-900">{stats.adminUsers}</p>
            </div>
          </div>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-md">
          <div className="flex items-center">
            <BarChart3 className="h-8 w-8 text-blue-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Companies</p>
              <p className="text-2xl font-semibold text-gray-900">{stats.companyUsers}</p>
            </div>
          </div>
        </div>
        <div className="bg-white p-4 rounded-lg shadow-md">
          <div className="flex items-center">
            <Users className="h-8 w-8 text-green-600" />
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-500">Job Seekers</p>
              <p className="text-2xl font-semibold text-gray-900">{stats.jobSeekerUsers}</p>
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

      {/* Search and Filters */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex flex-col md:flex-row gap-4">
          {/* Search */}
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={20} />
              <input
                type="text"
                placeholder="Search by name or email..."
                value={searchTerm}
                onChange={handleSearch}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              />
            </div>
          </div>

          {/* User Type Filter */}
          <div className="md:w-48">
            <select
              value={userTypeFilter}
              onChange={handleFilterChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">All User Types</option>
              <option value="JOB_SEEKER">Job Seeker</option>
              <option value="COMPANY">Company</option>
            </select>
          </div>
        </div>
      </div>

      {/* Users Table */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Name
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Email
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User Type
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Role
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
                    Loading users...
                  </td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-6 py-4 text-center text-gray-500">
                    No users found
                  </td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr key={user.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {user.id}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {user.fullName}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {user.email}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getUserTypeBadgeColor(user.userType)}`}>
                        {user.userType}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getRoleBadgeColor(user.role)}`}>
                        {user.role}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                        user.enabled ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {user.enabled ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex items-center gap-2">
                        {/* Email Button */}
                        <button
                          onClick={() => handleSendEmail(user.id, user.email)}
                          className="text-blue-600 hover:text-blue-900 p-1 rounded hover:bg-blue-50"
                          title="Send Email"
                        >
                          <Mail size={16} />
                        </button>

                        {/* Delete Button */}
                        {user.email !== 'BigBoss@example.com' && (
                          <button
                            onClick={() => handleDeleteUser(user.id, user.email)}
                            className="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50"
                            title="Delete User"
                          >
                            <Trash2 size={16} />
                          </button>
                        )}

                        {/* Promote/Demote Buttons - Only for Super Admin */}
                        {isSuperAdmin && user.email !== 'BigBoss@example.com' && (
                          <>
                            {user.role === 'USER' ? (
                              <button
                                onClick={() => handlePromoteUser(user.id, user.email)}
                                className="text-green-600 hover:text-green-900 p-1 rounded hover:bg-green-50"
                                title="Promote to Admin"
                              >
                                <UserPlus size={16} />
                              </button>
                            ) : (
                              <button
                                onClick={() => handleDemoteUser(user.id, user.email)}
                                className="text-orange-600 hover:text-orange-900 p-1 rounded hover:bg-orange-50"
                                title="Demote to User"
                              >
                                <UserMinus size={16} />
                              </button>
                            )}
                          </>
                        )}
                      </div>
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

export default UserManagement;