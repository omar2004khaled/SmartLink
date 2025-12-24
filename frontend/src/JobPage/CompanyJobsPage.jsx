import React, { useState } from 'react';
import { Plus } from 'lucide-react';
import JobFormModal from './JobFormModal';
import JobList from './JobList';

const CompanyJobsPage = ({ companyId }) => {
  const [showForm, setShowForm] = useState(false);
  const [editingJob, setEditingJob] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleSuccess = (msg) => {
    setSuccessMessage(msg);
    setRefreshTrigger(prev => prev + 1);
    setTimeout(() => {
      setSuccessMessage('');
    }, 3000);
  };

  const handleEdit = (job) => {
    setEditingJob(job);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingJob(null);
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">

        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Job Postings</h1>

          <button
            onClick={() => setShowForm(true)}
            className="flex items-center gap-2 bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
          >
            <Plus size={20} />
            New Job
          </button>
        </div>

        {/* Info banner */}
        <div className="mb-6 p-4 bg-blue-50 border-l-4 border-blue-500 rounded-r-lg">
          <p className="text-blue-800 font-medium">
            <strong>Tip:</strong> Double-click on any job card below to view all applications for that job
          </p>
        </div>

        {successMessage && (
          <div className="mb-6 bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg">
            {successMessage}
          </div>
        )}

        {showForm && (
          <JobFormModal
            onClose={handleCloseForm}
            onSuccess={handleSuccess}
            companyId={companyId}
            editingJob={editingJob}
          />
        )}

        <JobList
          companyId={companyId}
          refreshTrigger={refreshTrigger}
          onEdit={handleEdit}
        />
      </div>
    </div>
  );
};

export default CompanyJobsPage;
