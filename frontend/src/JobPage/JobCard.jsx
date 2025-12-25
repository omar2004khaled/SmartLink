import React from 'react';
import { Briefcase, MapPin, DollarSign, Calendar, Pencil, Trash2, Clock } from 'lucide-react';

const JobCard = ({ job, onEdit, onDelete }) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const formatSalary = (min, max) => {
    return `$${min.toLocaleString()} - $${max.toLocaleString()}`;
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-xl font-bold text-gray-900 mb-2">{job.title}</h3>
          <p className="text-sm text-gray-600">{job.companyName}</p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => onEdit(job)}
            className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
            title="Edit job"
          >
            <Pencil size={18} />
          </button>
          <button
            onClick={() => onDelete(job)}
            className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
            title="Delete job"
          >
            <Trash2 size={18} />
          </button>
        </div>
      </div>

      <p className="text-gray-700 mb-4 line-clamp-2">{job.description}</p>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <Briefcase size={16} />
          <span>{job.jobType.replace('_', ' ')}</span>
        </div>
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <MapPin size={16} />
          <span>{job.locationType}</span>
        </div>
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <DollarSign size={16} />
          <span>{formatSalary(job.salaryMin, job.salaryMax)}</span>
        </div>
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <Clock size={16} />
          <span>{job.experienceLevel}</span>
        </div>
      </div>

      <div className="flex items-center justify-between pt-4 border-t border-gray-200">
        <div className="flex items-center gap-2 text-sm text-gray-500">
          <Calendar size={16} />
          <span>Posted: {formatDate(job.createdAt)}</span>
        </div>
        <div className="text-sm text-gray-600">
          Deadline: <span className="font-medium">{formatDate(job.deadline)}</span>
        </div>
      </div>
    </div>
  );
};

export default JobCard;
