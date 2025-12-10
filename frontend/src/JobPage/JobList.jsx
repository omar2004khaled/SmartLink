import React, { useState, useEffect } from 'react';
import { Briefcase } from 'lucide-react';
import JobCard from './JobCard';
import DeleteConfirmModal from './DeleteConfirmModal';

const JobList = ({ companyId, refreshTrigger, onEdit }) => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteModal, setDeleteModal] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [activeTab, setActiveTab] = useState('current');

  const fetchJobs = async () => {
    setLoading(true);
    setError(null);
    if(companyId===null) return;
    try {
      const endpoint = activeTab === 'current' 
        ? `http://localhost:8080/jobs/company/current/${companyId}`
        : `http://localhost:8080/jobs/company/ended/${companyId}`;
      
      const response = await fetch(endpoint);
      if (!response.ok) throw new Error('Failed to fetch jobs');
      
      const data = await response.json();
      console.log(data);
      setJobs(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchJobs();
  }, [companyId, activeTab, refreshTrigger]);

  const handleDelete = async () => {
    if (!deleteModal) return;
    
    setDeleting(true);
    try {
      const response = await fetch(`http://localhost:8080/jobs/${deleteModal.jobId}`, {
        method: 'DELETE'
      });
      
      if (!response.ok) throw new Error('Failed to delete job');
      
      setJobs(jobs.filter(j => j.jobId !== deleteModal.jobId));
      setDeleteModal(null);
    } catch (err) {
      alert('Error deleting job: ' + err.message);
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-sm p-8 text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
        <p className="mt-4 text-gray-600">Loading jobs...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
        <p className="text-red-800">Error: {error}</p>
      </div>
    );
  }

  return (
    <>
      <div className="mb-6">
        <div className="flex gap-4 border-b border-gray-200">
          <button
            onClick={() => setActiveTab('current')}
            className={`px-4 py-2 font-medium ${
              activeTab === 'current'
                ? 'text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            Current Jobs
          </button>
          <button
            onClick={() => setActiveTab('ended')}
            className={`px-4 py-2 font-medium ${
              activeTab === 'ended'
                ? 'text-blue-600 border-b-2 border-blue-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            Ended Jobs
          </button>
        </div>
      </div>

      {jobs.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-8 text-center text-gray-500">
          <Briefcase size={48} className="mx-auto mb-4 text-gray-400" />
          <p>No {activeTab} jobs found</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {jobs.map(job => (
            <JobCard
              key={job.jobId}
              job={job}
              onEdit={onEdit}
              onDelete={setDeleteModal}
            />
          ))}
        </div>
      )}

      {deleteModal && (
        <DeleteConfirmModal
          job={deleteModal}
          onConfirm={handleDelete}
          onCancel={() => setDeleteModal(null)}
          loading={deleting}
        />
      )}
    </>
  );
};

export default JobList;