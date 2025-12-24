import React, { useState, useEffect } from 'react';
import { fetchJobs } from './Jobs';
import { submitApplication } from './jobService';
import Navbar from '../Navbar';
import JobFilters from './JobFilters';
import JobCard from './JobCard';
import ApplicationDialog from './ApplicationDialog';

const JobsPage = () => {
  const [showSearch, setShowSearch] = useState(false);
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    title: '',
    location: '',
    type: '',
    level: '',
    jobType: '',
    minSalary: '',
    maxSalary: ''
  });
  const [showApplyDialog, setShowApplyDialog] = useState(false);
  const [selectedJob, setSelectedJob] = useState(null);
  const [applicationData, setApplicationData] = useState({
    name: '',
    email: '',
    cv: null,
    coverLetter: ''
  });

  const handleJobClick = (job) => {
    const userRole = localStorage.getItem('userRole');
    if (userRole === 'COMPANY') {
      window.location.href = `/applications?jobId=${job.jobId}`;
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    console.log('Filters from form:', filters);
    setLoading(true);
    const data = await fetchJobs(filters);
    setJobs(data);
    setLoading(false);
  };

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  const handleApply = (job) => {
    setSelectedJob(job);
    setShowApplyDialog(true);
  };

  const handleApplicationChange = (e) => {
    const { name, value, files } = e.target;
    setApplicationData({ ...applicationData, [name]: files ? files[0] : value });
  };

  const handleSubmitApplication = (e) => {
    e.preventDefault();
    console.log('Application submitted:', applicationData, 'for job:', selectedJob);
    submitApplication(selectedJob.jobId, applicationData);
    setShowApplyDialog(false);
    setApplicationData({ name: '', email: '', cv: null, coverLetter: '' });
  };

  useEffect(() => {
    const loadJobs = async () => {
      setLoading(true);
      const data = await fetchJobs();
      setJobs(data);
      setLoading(false);
    };
    loadJobs();
  }, []);

  return (
    <>
      <Navbar/>
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-6 mt-20">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-800">Job Listings</h1>
          <button
            onClick={() => setShowSearch(!showSearch)}
            className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors duration-200"
          >
            {showSearch ? 'Hide Search' : 'Search Jobs'}
          </button>
        </div>

        <JobFilters 
          filters={filters} 
          handleFilterChange={handleFilterChange} 
          handleSearch={handleSearch} 
          showSearch={showSearch} 
        />

        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-600 text-lg">Loading jobs...</p>
          </div>
        ) : (
          <div className="space-y-4">
            {jobs.map((job) => (
              <JobCard
                key={job.jobId}
                job={job}
                handleJobClick={handleJobClick}
                handleApply={handleApply}
              />
            ))}
          </div>
        )}

        <ApplicationDialog
          showApplyDialog={showApplyDialog}
          selectedJob={selectedJob}
          applicationData={applicationData}
          handleApplicationChange={handleApplicationChange}
          handleSubmitApplication={handleSubmitApplication}
          setShowApplyDialog={setShowApplyDialog}
        />
      </div>
    </>
  );
};

export default JobsPage;