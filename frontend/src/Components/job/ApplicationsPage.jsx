import React, { useState, useEffect } from 'react';
import { fetchApplications, fetchApplicationsByUserId } from './applicationService';
import Navbar from '../Navbar';
import { useSearchParams } from 'react-router-dom';
import CompanyNavbar from '../CompanyNavbar';
import { API_BASE_URL } from '../../config';

const ApplicationsPage = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [comments, setComments] = useState({});
  const [searchParams] = useSearchParams();
  const [userType, setUserType] = useState(null);
  const jobId = searchParams.get('jobId');

  const handleCommentChange = (appId, value) => {
    setComments({ ...comments, [appId]: value });
  };

  // Save comment to backend
  const handleSaveComment = async (appId) => {
    const comment = comments[appId];
    
    if (!comment || comment.trim() === '') {
      console.error('Comment cannot be empty');
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/apply/add/comment`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          comment: comment,
          jobAppId: appId
        })
      });

      if (!response.ok) throw new Error('Failed to save comment');

      const updatedApp = await response.json();
      
      // Update the application in state
      setApplications(applications.map(app => 
        app.id === appId ? updatedApp : app
      ));
      
      // Clear the comment input
      setComments(prev => ({ ...prev, [appId]: '' }));
      
    } catch (error) {
      console.error('Error saving comment:', error);
    }
  };

  // Change application status
  const handleStatusChange = async (appId, newStatus) => {
    try {
      const response = await fetch(`${API_BASE_URL}/apply/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          status: newStatus,
          jobApp: appId
        })
      });

      if (!response.ok) throw new Error('Failed to update status');

      const updatedApp = await response.json();
      
      // Update the application in state
      setApplications(applications.map(app => 
        app.id === appId ? updatedApp : app
      ));
    
    } catch (error) {
      console.error('Error changing status:', error);
    }
  };

  const getStatusColor = (status) => {
    switch(status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-700 border-yellow-200';
      case 'ACCEPTED': return 'bg-green-100 text-green-700 border-green-200';
      case 'REJECTED': return 'bg-red-100 text-red-700 border-red-200';
      default: return 'bg-gray-100 text-gray-700 border-gray-200';
    }
  };

  useEffect(() => {
    const loadApplications = async () => {
      setLoading(true);
      let data = [];
      
      // Get user type from localStorage
      const type = localStorage.getItem('userType');
      setUserType(type);
      //console.log(type);
      const userId = localStorage.getItem('userId');
      
      if (type === 'USER' && userId) {
        // User is fetching their own applications
        data = await fetchApplicationsByUserId(userId);
      } else if (jobId) {
        // Company is fetching applications for a specific job
        data = await fetchApplications(jobId);
      }
      
      setApplications(data);
      setLoading(false);
    };
    loadApplications();
  }, []);

  return (
    <>
      {userType === 'USER' ? <Navbar /> : <CompanyNavbar />}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 mt-20">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">Job Applications</h1>

        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-600 text-lg">Loading applications...</p>
          </div>
        ) : (
          <div className="space-y-4">
            {applications.map((app) => (
              <div key={app.id} className="bg-white rounded-lg p-6 shadow-sm border border-gray-200">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="text-xl font-bold text-gray-900">{app.name}</h3>
                    <p className="text-gray-600 text-sm">{app.email}</p>
                    {app.companyName && (
                      <p className="text-gray-500 text-sm mt-1">Company: {app.companyName}</p>
                    )}
                    <p className="text-gray-500 text-xs mt-1">Applied: {new Date(app.createdAt).toLocaleString()}</p>
                    <span className={`inline-block mt-2 px-3 py-1 rounded-md text-xs font-medium border ${getStatusColor(app.status)}`}>
                      {app.status}
                    </span>
                  </div>
                  <div className="flex gap-2">
                    {userType === 'COMPANY' && (
                      <select
                        value={app.status}
                        onChange={(e) => handleStatusChange(app.id, e.target.value)}
                        className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                      >
                        <option value="PENDING">Pending</option>
                        <option value="REVIEWED">Reviewed</option>
                        <option value="ACCEPTED">Accepted</option>
                        <option value="REJECTED">Rejected</option>
                      </select>
                    )}
                    <a
                      href={app.cvURL}
                      download="CV.pdf"
                      className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg transition-colors"
                    >
                      Download CV
                    </a>
                  </div>
                </div>
                
                {app.coverLetter && (
                  <div className="mt-4">
                    <h4 className="text-sm font-semibold text-gray-700 mb-2">Cover Letter:</h4>
                    <p className="text-gray-600 text-sm leading-relaxed whitespace-pre-wrap">
                      {app.coverLetter}
                    </p>
                  </div>
                )}

                {/* Display existing comments from API */}
                {app.comments && app.comments.length > 0 && (
                  <div className="mt-4 bg-gray-50 p-4 rounded-lg border border-gray-200">
                    <h4 className="text-sm font-semibold text-gray-700 mb-3">Comments:</h4>
                    <div className="space-y-3">
                      {app.comments.map((comment, idx) => (
                        <div key={idx} className="bg-white p-3 rounded border border-gray-100">
                          <p className="text-gray-600 text-sm leading-relaxed">{comment}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {userType === 'COMPANY' && (
                  <div className="mt-4">
                    <h4 className="text-sm font-semibold text-gray-700 mb-2">Add Comment:</h4>
                    <textarea
                      value={comments[app.id] || ''}
                      onChange={(e) => handleCommentChange(app.id, e.target.value)}
                      placeholder="Add your comment here..."
                      rows="3"
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none text-sm"
                    />
                    <button
                      onClick={() => handleSaveComment(app.id)}
                      className="mt-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white text-sm font-medium rounded-lg transition-colors"
                    >
                      Save Comment
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
};

export default ApplicationsPage;
