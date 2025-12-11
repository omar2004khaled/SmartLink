import React, { useState, useEffect } from 'react';
import { fetchJobs } from './Jobs';
import { submitApplication } from './jobService';
import Navbar from '../Navbar';

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

        {showSearch && (
          <div className="bg-white rounded-lg p-6 mb-6 shadow-sm border border-gray-200">
            <h2 className="text-xl font-semibold text-gray-800 mb-4">Search Filters</h2>
            <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <input
                type="text"
                name="title"
                placeholder="Job Title"
                value={filters.title}
                onChange={handleFilterChange}
                className="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
              />
              <input
                type="text"
                name="location"
                placeholder="Location"
                value={filters.location}
                onChange={handleFilterChange}
                className="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
              />
              <select
                name="type"
                value={filters.type}
                onChange={handleFilterChange}
                className="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-white"
              >
                <option value="">Location Type</option>
                <option value="REMOTE">Remote</option>
                <option value="ONSITE">On-site</option>
                <option value="HYBRID">Hybrid</option>
              </select>
              <select
                name="level"
                value={filters.level}
                onChange={handleFilterChange}
                className="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-white"
              >
                <option value="">Experience Level</option>
                <option value="JUNIOR">junior Level</option>
                <option value="ENTRY">Entry Level</option>
                <option value="MID">Mid Level</option>
                <option value="SENIOR">Senior Level</option>
                <option value="LEAD">Lead Level</option>
                <option value="PRINCIPAL">Principal Level</option>
              </select>
              <select
                name="jobType"
                value={filters.jobType}
                onChange={handleFilterChange}
                className="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-white"
              >
                <option value="">Job Type</option>
                <option value="FULL_TIME">Full-time</option>
                <option value="PART_TIME">Part-time</option>
                <option value="CONTRACT">Contract</option>
                <option value="TEMPORARY">Temporary</option>
                <option value="INTERNSHIP">Intern</option>
                <option value="FREELANCE">Freelance</option>
              </select>
              <input
                type="number"
                name="minSalary"
                placeholder="Min Salary"
                value={filters.minSalary}
                onChange={handleFilterChange}
                className="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
              />
              <input
                type="number"
                name="maxSalary"
                placeholder="Max Salary"
                value={filters.maxSalary}
                onChange={handleFilterChange}
                className="px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
              />
              <button
                type="submit"
                className="md:col-span-2 lg:col-span-3 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors duration-200"
              >
                Apply Filters
              </button>
            </form>
          </div>
        )}

        {loading ? (
          <div className="text-center py-12">
            <p className="text-gray-600 text-lg">Loading jobs...</p>
          </div>
        ) : (
          <div className="space-y-4">
            {jobs.map((job) => (
              <div
                key={job.id}
                className="bg-white rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow duration-200 border border-gray-200"
              >
                <h3 className="text-xl font-bold text-gray-900 mb-2">{job.title}</h3>
                <p className="text-gray-600 mb-4 text-sm leading-relaxed">{job.description}</p>
                <div className="flex flex-wrap gap-2 mb-4">
                  <span className="px-3 py-1.5 bg-blue-50 text-blue-700 rounded-md text-xs font-medium border border-blue-100">
                    📍 {job.locationType === 'REMOTE' ? '(REMOTE)' : job.locationType === 'ONSITE' ? '(ONSITE)' : '(HYBRID)'}
                  </span>
                  <span className="px-3 py-1.5 bg-purple-50 text-purple-700 rounded-md text-xs font-medium border border-purple-100">
                    💼 {job.experienceLevel === 'MID_LEVEL' ? 'MID Level' : job.experienceLevel === 'ENTRY_LEVEL' ? 'Entry Level' : 'Senior Level'}
                  </span>
                  <span className="px-3 py-1.5 bg-green-50 text-green-700 rounded-md text-xs font-medium border border-green-100">
                    💰 ${job.salaryMin} - ${job.salaryMax}
                  </span>
                  <span className="px-3 py-1.5 bg-orange-50 text-orange-700 rounded-md text-xs font-medium border border-orange-100">
                    ⏰ {job.jobType === "FULL_TIME" ? "Full time" : "Part time"}
                  </span>
                  {job.deadline && (
                    <span className="px-3 py-1.5 bg-red-50 text-red-700 rounded-md text-xs font-medium border border-red-100">
                      📅 Deadline: {new Date(job.deadline).toLocaleDateString()}
                    </span>
                  )}
                </div>
                <button
                  onClick={() => handleApply(job)}
                  className="w-full px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors duration-200"
                >
                  Apply Now
                </button>
              </div>
            ))}
          </div>
        )}

        {showApplyDialog && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg p-8 max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-xl">
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Apply for {selectedJob?.title}</h2>
              <form onSubmit={handleSubmitApplication} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">Full Name</label>
                  <input
                    type="text"
                    name="name"
                    value={applicationData.name}
                    onChange={handleApplicationChange}
                    required
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">Email</label>
                  <input
                    type="email"
                    name="email"
                    value={applicationData.email}
                    onChange={handleApplicationChange}
                    required
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">CV/Resume</label>
                  <input
                    type="file"
                    name="cv"
                    onChange={handleApplicationChange}
                    accept=".pdf,.doc,.docx"
                    required
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-medium file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1.5">Cover Letter</label>
                  <textarea
                    name="coverLetter"
                    value={applicationData.coverLetter}
                    onChange={handleApplicationChange}
                    rows="5"
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all resize-none"
                  />
                </div>
                <div className="flex gap-3 pt-4">
                  <button
                    type="submit"
                    className="flex-1 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors duration-200"
                  >
                    Submit Application
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowApplyDialog(false)}
                    className="flex-1 px-5 py-2.5 bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium rounded-lg transition-colors duration-200"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default JobsPage;