import React from 'react';

const JobCard = ({ job, handleJobClick, handleApply }) => {
  return (
    <div
      onDoubleClick={() => handleJobClick(job)}
      className="bg-white rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow duration-200 border border-gray-200 cursor-pointer"
    >
      <div className="flex justify-between items-start mb-2">
        <h3 className="text-xl font-bold text-gray-900">{job.title}</h3>
        <span className="text-sm text-gray-500">company name: {job.companyName}</span>
      </div>
      <p className="text-gray-600 mb-4 text-sm leading-relaxed">{job.description}</p>
      <div className="flex flex-wrap gap-2 mb-4">
        <span className="px-3 py-1.5 bg-blue-50 text-blue-700 rounded-md text-xs font-medium border border-blue-100">
          📍 {job.locationType}
        </span>
        <span className="px-3 py-1.5 bg-purple-50 text-purple-700 rounded-md text-xs font-medium border border-purple-100">
          💼 {job.experienceLevel}
        </span>
        <span className="px-3 py-1.5 bg-green-50 text-green-700 rounded-md text-xs font-medium border border-green-100">
          💰 ${job.salaryMin} - ${job.salaryMax}
        </span>
        <span className="px-3 py-1.5 bg-orange-50 text-orange-700 rounded-md text-xs font-medium border border-orange-100">
          ⏰ {job.jobType}
        </span>
        {job.deadline && (
          <span className="px-3 py-1.5 bg-red-50 text-red-700 rounded-md text-xs font-medium border border-red-100">
            📅 Deadline: {new Date(job.deadline).toLocaleDateString()}
          </span>
        )}
      </div>
      <div className="text-sm text-gray-600 mb-4">
        📍 {job.jobLocation}
      </div>
      <button
        onClick={() => handleApply(job)}
        disabled={job.isApplied}
        className={`w-full px-5 py-2.5 font-medium rounded-lg transition-colors duration-200 ${
          job.isApplied
            ? 'bg-gray-400 text-gray-700 cursor-not-allowed'
            : 'bg-blue-600 hover:bg-blue-700 text-white'
        }`}
      >
        {job.isApplied ? 'Already Applied' : 'Apply Now'}
      </button>
    </div>
  );
};

export default JobCard;
