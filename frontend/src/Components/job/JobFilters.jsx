import React from 'react';

const JobFilters = ({ filters, handleFilterChange, handleSearch, showSearch }) => {
  return (
    showSearch && (
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
    )
  );
};

export default JobFilters;
