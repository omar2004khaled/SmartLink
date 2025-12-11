import React from 'react';

const JobFormField = ({
  label,
  name,
  value,
  onChange,
  error,
  type = 'text',
  placeholder,
  options = []
}) => {
  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-2">{label}</label>

      {type === 'select' ? (
        <select
          name={name}
          value={value}
          onChange={onChange}
          className={`w-full px-4 py-2 border rounded-lg ${
            error ? 'border-red-500' : 'border-gray-300'
          }`}
        >
          <option value="">Select...</option>
          {options.map(opt => (
            <option key={opt} value={opt}>{opt}</option>
          ))}
        </select>
      ) : type === 'textarea' ? (
        <textarea
          name={name}
          value={value}
          onChange={onChange}
          rows="4"
          className={`w-full px-4 py-2 border rounded-lg ${
            error ? 'border-red-500' : 'border-gray-300'
          }`}
        ></textarea>
      ) : (
        <input
          type={type}
          name={name}
          value={value}
          placeholder={placeholder}
          onChange={onChange}
          className={`w-full px-4 py-2 border rounded-lg ${
            error ? 'border-red-500' : 'border-gray-300'
          }`}
        />
      )}

      {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
    </div>
  );
};

export default JobFormField;
