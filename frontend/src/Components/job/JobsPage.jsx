import React, { useState, useEffect } from 'react';
import { fetchJobs } from './Jobs';
import { submitApplication } from './jobService';
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
    submitApplication(selectedJob.jobId, applicationData)
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
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-4xl font-bold text-gray-900">Job Listings</h1>
        <button
          onClick={() => setShowSearch(!showSearch)}
          className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors duration-200 shadow-md hover:shadow-lg"
        >
          {showSearch ? 'Hide Search' : 'Search Jobs'}
        </button>
      </div>

      {showSearch && (
        <div className="bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl p-6 mb-8 shadow-lg border border-gray-200">
          <h2 className="text-2xl font-semibold text-gray-800 mb-6">Search Filters</h2>
          <form onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <input
              type="text"
              name="title"
              placeholder="Job Title"
              value={filters.title}
              onChange={handleFilterChange}
              className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
            />
            <input
              type="text"
              name="location"
              placeholder="Location"
              value={filters.location}
              onChange={handleFilterChange}
              className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
            />
            <select
              name="type"
              value={filters.type}
              onChange={handleFilterChange}
              className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-white"
            >
              <option value="">Location Type</option>
              <option value="remote">Remote</option>
              <option value="onsite">On-site</option>
              <option value="hybrid">Hybrid</option>
            </select>
            <select
              name="level"
              value={filters.level}
              onChange={handleFilterChange}
              className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-white"
            >
              <option value="">Experience Level</option>
              <option value="entry_level">Entry</option>
              <option value="mid">Mid</option>
              <option value="senior">Senior</option>
            </select>
            <select
              name="jobType"
              value={filters.jobType}
              onChange={handleFilterChange}
              className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all bg-white"
            >
              <option value="">Job Type</option>
              <option value="full_time">Full-time</option>
              <option value="part_time">Part-time</option>
            </select>
            <input
              type="number"
              name="minSalary"
              placeholder="Min Salary"
              value={filters.minSalary}
              onChange={handleFilterChange}
              className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
            />
            <input
              type="number"
              name="maxSalary"
              placeholder="Max Salary"
              value={filters.maxSalary}
              onChange={handleFilterChange}
              className="px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
            />
            <button
              type="submit"
              className="px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-medium rounded-lg transition-colors duration-200 shadow-md hover:shadow-lg"
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
        <div className="grid gap-6">
          {jobs.map((job) => (
          <div
            key={job.id}
            className="bg-white rounded-xl p-6 shadow-md hover:shadow-xl transition-shadow duration-300 border border-gray-200"
          >
            <h3 className="text-2xl font-bold text-gray-900 mb-3">{job.title}</h3>
            <p className="text-gray-600 mb-4 leading-relaxed">{job.description}</p>
            <div className="flex flex-wrap gap-3">
              <span className="px-4 py-2 bg-blue-100 text-blue-700 rounded-lg text-sm font-medium">
                📍 {job.jobLlocation} ({job.locationType})
              </span>
              <span className="px-4 py-2 bg-purple-100 text-purple-700 rounded-lg text-sm font-medium">
                💼 {job.experienceLevel} Level
              </span>
              <span className="px-4 py-2 bg-green-100 text-green-700 rounded-lg text-sm font-medium">
                💰 ${job.salaryMin} - ${job.salaryMax}
              </span>
              <span className="px-4 py-2 bg-orange-100 text-orange-700 rounded-lg text-sm font-medium">
                ⏰ {job.jobType=="FULL_TIME"? "Full time":"job.jobType"}
              </span>
            </div>
            <button
              onClick={() => handleApply(job)}
              className="mt-4 w-full px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors duration-200 shadow-md hover:shadow-lg"
            >
              Apply Now
            </button>
          </div>
        ))}
        </div>
      )}

      {showApplyDialog && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl p-8 max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
            <h2 className="text-3xl font-bold text-gray-900 mb-6">Apply for {selectedJob?.title}</h2>
            <form onSubmit={handleSubmitApplication} className="space-y-5">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Full Name</label>
                <input
                  type="text"
                  name="name"
                  value={applicationData.name}
                  onChange={handleApplicationChange}
                  required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
                <input
                  type="email"
                  name="email"
                  value={applicationData.email}
                  onChange={handleApplicationChange}
                  required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">CV/Resume</label>
                <input
                  type="file"
                  name="cv"
                  onChange={handleApplicationChange}
                  accept=".pdf,.doc,.docx"
                  required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Cover Letter</label>
                <textarea
                  name="coverLetter"
                  value={applicationData.coverLetter}
                  onChange={handleApplicationChange}
                  rows="6"
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all resize-none"
                />
              </div>
              <div className="flex gap-4 pt-4">
                <button
                  type="submit"
                  className="flex-1 px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors duration-200 shadow-md hover:shadow-lg"
                >
                  Submit Application
                </button>
                <button
                  type="button"
                  onClick={() => setShowApplyDialog(false)}
                  className="flex-1 px-6 py-3 bg-gray-300 hover:bg-gray-400 text-gray-800 font-medium rounded-lg transition-colors duration-200"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default JobsPage;
