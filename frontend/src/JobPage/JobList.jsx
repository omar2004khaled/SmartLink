import React, { useState, useEffect } from 'react';
import { Briefcase, ChevronLeft, ChevronRight } from 'lucide-react';
import JobCard from './JobCard';
import DeleteConfirmModal from './DeleteConfirmModal';
import { API_BASE_URL } from '../config';
import { useAlert } from '../hooks/useAlert';

const JobList = ({ companyId, refreshTrigger, onEdit }) => {
  const { showError, showSuccess } = useAlert();
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteModal, setDeleteModal] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [activeTab, setActiveTab] = useState('current');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  const fetchJobs = async () => {
    setLoading(true);
    setError(null);
    //console.log(companyId)
    if (companyId === null) return;
    try {
      const endpoint = activeTab === 'current'
        ? `${API_BASE_URL}/jobs/company/current/${companyId}?page=${currentPage}&size=${pageSize}`
        : `${API_BASE_URL}/jobs/company/ended/${companyId}?page=${currentPage}&size=${pageSize}`;

      const response = await fetch(endpoint);
      if (!response.ok) throw new Error('Failed to fetch jobs');

      const data = await response.json();
      //console.log(data);
      setJobs(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
      setCurrentPage(data.number);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchJobs();
  }, [companyId, activeTab, refreshTrigger, currentPage, pageSize]);
  useEffect(() => {
    setCurrentPage(0);
  }, [activeTab]);

  const handleDelete = async () => {
    if (!deleteModal) return;

    setDeleting(true);
    try {
      const response = await fetch(`${API_BASE_URL}/jobs/${deleteModal.jobId}`, {
        method: 'DELETE'
      });

      if (!response.ok) throw new Error('Failed to delete job');

      if (jobs.length === 1 && currentPage > 0) {
        setCurrentPage(currentPage - 1);
      } else {
        fetchJobs();
      }

      setDeleteModal(null);
      showSuccess('Job deleted successfully!');
    } catch (err) {
      showError('Error deleting job: ' + err.message);
    } finally {
      setDeleting(false);
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setCurrentPage(newPage);
    }
  };

  const handlePageSizeChange = (e) => {
    setPageSize(parseInt(e.target.value));
    setCurrentPage(0);
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
            className={`px-4 py-2 font-medium ${activeTab === 'current'
              ? 'text-blue-600 border-b-2 border-blue-600'
              : 'text-gray-600 hover:text-gray-900'
              }`}
          >
            Current Jobs
          </button>
          <button
            onClick={() => setActiveTab('ended')}
            className={`px-4 py-2 font-medium ${activeTab === 'ended'
              ? 'text-blue-600 border-b-2 border-blue-600'
              : 'text-gray-600 hover:text-gray-900'
              }`}
          >
            Ended Jobs
          </button>
        </div>
      </div>

      {totalElements > 0 && (
        <div className="flex justify-between items-center mb-4">
          <div className="flex items-center gap-2">
            <label htmlFor="pageSize" className="text-sm text-gray-600">
              Jobs per page:
            </label>
            <select
              id="pageSize"
              value={pageSize}
              onChange={handlePageSizeChange}
              className="px-3 py-1 border border-gray-300 rounded-lg text-sm"
            >
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>
          </div>
        </div>
      )}

      {jobs.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-8 text-center text-gray-500">
          <Briefcase size={48} className="mx-auto mb-4 text-gray-400" />
          <p>No {activeTab} jobs found</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
            {jobs.map(job => (
              <div
                key={job.jobId}
                onDoubleClick={() => window.location.href = `/applications?jobId=${job.jobId}`}
                style={{ cursor: 'pointer' }}
              >
                <JobCard
                  job={job}
                  onEdit={onEdit}
                  onDelete={setDeleteModal}
                />
              </div>
            ))}
          </div>
          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-2 mt-6">
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="p-2 rounded-lg border border-gray-300 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                title="Previous page"
              >
                <ChevronLeft size={20} />
              </button>

              <div className="flex gap-1">
                {[...Array(totalPages)].map((_, index) => {
                  const showPage =
                    index === 0 ||
                    index === totalPages - 1 ||
                    (index >= currentPage - 1 && index <= currentPage + 1);

                  const showEllipsis =
                    (index === currentPage - 2 && currentPage > 2) ||
                    (index === currentPage + 2 && currentPage < totalPages - 3);

                  if (showEllipsis) {
                    return (
                      <span key={index} className="px-3 py-2 text-gray-500">
                        ...
                      </span>
                    );
                  }

                  if (!showPage) return null;

                  return (
                    <button
                      key={index}
                      onClick={() => handlePageChange(index)}
                      className={`px-4 py-2 rounded-lg border ${currentPage === index
                        ? 'bg-blue-600 text-white border-blue-600'
                        : 'border-gray-300 hover:bg-gray-50'
                        }`}
                    >
                      {index + 1}
                    </button>
                  );
                })}
              </div>

              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages - 1}
                className="p-2 rounded-lg border border-gray-300 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                title="Next page"
              >
                <ChevronRight size={20} />
              </button>
            </div>
          )}
        </>
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
