import React, { useState } from 'react';
import { Plus } from 'lucide-react';
import JobFormModal from './JobFormModal';
import JobList from './JobList';

const CompanyJobsPage = ({ companyId }) => {
  const [showForm, setShowForm] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const handleSuccess = (msg) => {
    setSuccessMessage(msg);
    setTimeout(() => {
      setSuccessMessage('');
    }, 3000);
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

        {successMessage && (
          <div className="mb-6 bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg">
            {successMessage}
          </div>
        )}

        {showForm && (
          <JobFormModal
            onClose={() => setShowForm(false)}
            onSuccess={handleSuccess}
            companyId={companyId}
          />
        )}

        <JobList />
      </div>
    </div>
  );
};

export default CompanyJobsPage;
