import React, { useState } from 'react';
import { X } from 'lucide-react';
import JobFormField from './JobFormField';

const JobFormModal = ({ onClose, onSuccess,companyId}) => {
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  
  const [formData, setFormData] = useState({
    companyId: companyId,
    title: '',
    description: '',
    experienceLevel: '',
    jobType: '',
    locationType: '',
    jobLocation: '',
    salaryMin: '',
    salaryMax: '',
    deadline: ''
  });

  const experienceLevels = ['ENTRY', 'JUNIOR', 'MID', 'SENIOR', 'LEAD', 'EXECUTIVE'];
  const jobTypes = ['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'FREELANCE'];
  const locationTypes = ['ONSITE', 'REMOTE','HYBRID'];

  const validateForm = () => {
    const today = new Date().toISOString().split("T")[0];
    const newErrors = {};
    if (!formData.title.trim()) newErrors.title = 'Job title is required';
    if (!formData.description.trim()) newErrors.description = 'Job description is required';
    if (!formData.experienceLevel) newErrors.experienceLevel = 'Experience level is required';
    if (!formData.jobType) newErrors.jobType = 'Job type is required';
    if (!formData.locationType) newErrors.locationType = 'Location type is required';
    if (!formData.jobLocation.trim()) newErrors.jobLocation = 'Job location is required';
    if (!formData.salaryMin) newErrors.salaryMin = 'Minimum salary is required';
    if (!formData.salaryMax) newErrors.salaryMax = 'Maximum salary is required';
    if (!formData.deadline) newErrors.deadline = 'Application deadline is required';
    if(formData.salaryMax<formData.salaryMin)newErrors.salaryMax='must be greater than minimum salary'
    if(formData.salaryMax<0)newErrors.salaryMax='must be positive value'
    if(formData.salaryMin<0)newErrors.salaryMax='must be positive value'
    if (formData.deadline < today)newErrors.deadline = 'Not valid deadline'; 

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    setLoading(true);

    try {
      const payload = {
        ...formData,
        salaryMin: parseInt(formData.salaryMin),
        salaryMax: parseInt(formData.salaryMax),
        deadline: new Date(formData.deadline).toISOString()
      };

      const response = await fetch('http://localhost:8080/jobs/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) throw new Error('Failed to create job');

      onSuccess('Job posted successfully!');
      onClose();

    } catch (err) {
      setErrors({ submit: err.message });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-6 z-50">
      <div className="bg-white rounded-xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">

        <div className="flex justify-between items-center p-6 border-b">
          <h2 className="text-2xl font-bold text-gray-900">New Job</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X size={24} />
          </button>
        </div>

        <div className="p-6 space-y-6">

          {errors.submit && (
            <div className="bg-red-50 border-red-200 text-red-800 px-4 py-3 rounded-lg">
              {errors.submit}
            </div>
          )}

          <JobFormField
            label="Job Title *"
            name="title"
            value={formData.title}
            onChange={handleChange}
            error={errors.title}
            placeholder="e.g., Senior Backend Developer"
          />

          <JobFormField
            label="Job Description *"
            name="description"
            type="textarea"
            value={formData.description}
            onChange={handleChange}
            error={errors.description}
          />

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

            <JobFormField
              label="Experience Level *"
              name="experienceLevel"
              type="select"
              options={experienceLevels}
              value={formData.experienceLevel}
              onChange={handleChange}
              error={errors.experienceLevel}
            />

            <JobFormField
              label="Job Type *"
              name="jobType"
              type="select"
              options={jobTypes}
              value={formData.jobType}
              onChange={handleChange}
              error={errors.jobType}
            />

            <JobFormField
              label="Location Type *"
              name="locationType"
              type="select"
              options={locationTypes}
              value={formData.locationType}
              onChange={handleChange}
              error={errors.locationType}
            />

            <JobFormField
              label="Job Location *"
              name="jobLocation"
              value={formData.jobLocation}
              onChange={handleChange}
              error={errors.jobLocation}
            />

            <JobFormField
              label="Minimum Salary ($)*"
              name="salaryMin"
              type="number"
              value={formData.salaryMin}
              onChange={handleChange}
              error={errors.salaryMin}
            />

            <JobFormField
              label="Maximum Salary ($)*"
              name="salaryMax"
              type="number"
              value={formData.salaryMax}
              onChange={handleChange}
              error={errors.salaryMax}
            />

          </div>

          <JobFormField
            label="Application Deadline *"
            name="deadline"
            type="date"
            value={formData.deadline}
            onChange={handleChange}
            error={errors.deadline}
          />

          <div className="flex justify-end gap-4 pt-4 border-t">
            <button
              onClick={onClose}
              className="px-6 py-2 border border-gray-300 rounded-lg"
              disabled={loading}
            >
              Cancel
            </button>

            <button
              onClick={handleSubmit}
              disabled={loading}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg"
            >
              {loading ? 'Posting...' : 'Post Job'}
            </button>
          </div>

        </div>

      </div>
    </div>
  );
};

export default JobFormModal;
