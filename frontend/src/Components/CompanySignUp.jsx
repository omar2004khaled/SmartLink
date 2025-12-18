import React, { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';

const CompanySignUp = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    companyName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    industry: '',
    website: '',
    founded: ''
  });

  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState('');

  const validateField = (name, value) => {
    switch (name) {
      case 'companyName':
        if (!value.trim()) return 'Company name is required';
        if (value.trim().length < 2) return 'Company name must be at least 2 characters';
        return '';

      case 'email':
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!value) return 'Email is required';
        if (!emailRegex.test(value)) return 'Please enter a valid email';
        return '';

      case 'phone':
        const phoneRegex = /^[\d\s\-\(\)]+$/;
        if (!value) return 'Phone number is required';
        if (!phoneRegex.test(value)) return 'Please enter a valid phone number';
        if (value.replace(/\D/g, '').length < 10) return 'Phone number must be at least 10 digits';
        return '';

      case 'password':
        if (!value) return 'Password is required';
        if (value.length < 8) return 'Password must be at least 8 characters';
        if (!/(?=.*[0-9])/.test(value)) return 'Password must contain at least one number';
        if (!/(?=.*[A-Z])/.test(value)) return 'Password must contain at least one uppercase letter';
        return '';

      case 'confirmPassword':
        if (!value) return 'Please confirm your password';
        if (value !== formData.password) return 'Passwords do not match';
        return '';

      case 'founded':
        if (value && (value < 1800 || value > new Date().getFullYear())) {
          return 'Please enter a valid year';
        }
        return '';

      default:
        return '';
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    if (apiError) setApiError('');

    if (errors[name]) {
      const error = validateField(name, value);
      setErrors(prev => ({
        ...prev,
        [name]: error
      }));
    }
  };

  const handleBlur = (e) => {
    const { name, value } = e.target;
    const error = validateField(name, value);
    setErrors(prev => ({
      ...prev,
      [name]: error
    }));
  };

  const registerCompany = async () => {
    setLoading(true);
    setApiError('');

    try {
      const response = await fetch("http://localhost:8080/auth/company/register", {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Accept": "application/json"
        },
        mode: 'cors',
        body: JSON.stringify({
          companyName: formData.companyName.trim(),
          email: formData.email.toLowerCase().trim(),
          password: formData.password,
          confirmPassword: formData.confirmPassword,
          phoneNumber: formData.phone.replace(/\D/g, ''),
          industry: formData.industry || null,
          website: formData.website || null,
          founded: formData.founded ? parseInt(formData.founded) : null
        })
      });

      const responseText = await response.text();
      let data;
      try {
        data = JSON.parse(responseText);
      } catch (e) {
        data = { 
          message: responseText || 'No response from server',
          success: response.ok 
        };
      }

      if (!response.ok) {
        let errorMessage = 'Registration failed';
        
        if (typeof data === 'string') {
          errorMessage = data;
        } else if (data.message) {
          errorMessage = data.message;
        } else if (data.error) {
          errorMessage = data.error;
        }

        if (response.status === 409) {
          setApiError('This email is already registered. Please use a different email or try logging in.');
        } else if (response.status === 400) {
          setApiError(errorMessage);
        } else {
          setApiError(`${errorMessage} (Status: ${response.status})`);
        }
        
        return;
      }

      console.log("Company registration successful, redirecting to verification...");
      navigate('/verify-email', { 
        state: { 
          email: formData.email,
          justRegistered: true
        } 
      });
        
    } catch (error) {
      console.error("Registration Error:", error);
      
      if (error.message === 'Failed to fetch') {
        setApiError('Cannot connect to server. Please check if the backend is running and try again.');
      } else {
        setApiError(`An unexpected error occurred: ${error.message}`);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setApiError('');

    const newErrors = {};
    ['companyName', 'email', 'phone', 'password', 'confirmPassword', 'founded'].forEach(key => {
      const error = validateField(key, formData[key]);
      if (error) newErrors[key] = error;
    });

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      registerCompany();
    } else {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  return (
    <div className="relative flex min-h-screen w-full flex-col overflow-x-hidden">
      <div className="flex-grow flex items-stretch">
        <div className="w-full grid grid-cols-1 md:grid-cols-2">
          {/* Left Panel */}
          <div className="hidden md:flex flex-col justify-between p-12 bg-[#FFEAEE] dark:bg-[#2C1A1D] text-center">
            <div className="self-start">
              <div className="flex items-center gap-2">
                <img 
                  src="src/assets/Logo.png" 
                  alt="Logo"
                  className="h-12 w-auto object-contain"
                />
                <span className="text-2xl font-bold text-gray-800 dark:text-gray-200">Smart Link</span>
              </div>
            </div>
            <div className="flex flex-col gap-6">
              <h1 className="text-gray-800 dark:text-gray-200 text-5xl font-black leading-tight tracking-[-0.033em]">
                Grow Your Business
              </h1>
              <h2 className="text-gray-700 dark:text-gray-300 text-base font-normal leading-normal">
                Connect with top talent and build your company's presence
              </h2>
            </div>
            <div></div>
          </div>

          {/* Right Panel - Form */}
          <div className="w-full flex items-center justify-center p-6 sm:p-8 lg:p-12 bg-white dark:bg-[#1a1a1a]">
            <div className="w-full max-w-md">
              <div className="mb-8 text-left">
                <h1 className="text-3xl font-black leading-tight tracking-[-0.033em] text-[#333333] dark:text-[#F5F5F5]">
                  Create Company Account
                </h1>
              </div>

              {/* Error Message */}
              {apiError && (
                <div className="mb-4 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg flex items-start gap-3">
                  <svg className="w-5 h-5 text-red-600 dark:text-red-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <div className="flex-1">
                    <p className="text-sm text-red-700 dark:text-red-300 mt-1">{apiError}</p>
                  </div>
                  <button 
                    onClick={() => setApiError('')}
                    className="text-red-400 hover:text-red-600 dark:text-red-500 dark:hover:text-red-300"
                  >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              )}

              <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                {/* Company Name */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Company Name *</p>
                  <input
                    name="companyName"
                    value={formData.companyName}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.companyName ? 'border-red-500' : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="e.g. Acme Corporation"
                  />
                  {errors.companyName && (
                    <p className="text-xs text-red-500 mt-1">{errors.companyName}</p>
                  )}
                </label>

                {/* Email */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Company Email *</p>
                  <input
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.email ? 'border-red-500' : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="contact@company.com"
                  />
                  {errors.email && (
                    <p className="text-xs text-red-500 mt-1">{errors.email}</p>
                  )}
                </label>

                {/* Phone Number */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Phone Number *</p>
                  <input
                    name="phone"
                    type="tel"
                    value={formData.phone}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.phone ? 'border-red-500' : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="(123) 456-7890"
                  />
                  {errors.phone && (
                    <p className="text-xs text-red-500 mt-1">{errors.phone}</p>
                  )}
                </label>

                {/* Industry */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Industry</p>
                  <input
                    name="industry"
                    value={formData.industry}
                    onChange={handleChange}
                    className="form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border border-[#CCCCCC] dark:border-[#444444] bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal"
                    placeholder="e.g. Technology, Healthcare"
                  />
                </label>

                {/* Website */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Website</p>
                  <input
                    name="website"
                    type="url"
                    value={formData.website}
                    onChange={handleChange}
                    className="form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border border-[#CCCCCC] dark:border-[#444444] bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal"
                    placeholder="https://www.company.com"
                  />
                </label>

                {/* Founded Year */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Founded Year</p>
                  <input
                    name="founded"
                    type="number"
                    value={formData.founded}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    min="1800"
                    max={new Date().getFullYear()}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.founded ? 'border-red-500' : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="e.g. 2020"
                  />
                  {errors.founded && (
                    <p className="text-xs text-red-500 mt-1">{errors.founded}</p>
                  )}
                </label>

                {/* Password */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Password *</p>
                  <div className="relative flex w-full flex-1 items-stretch">
                    <input
                      name="password"
                      type={showPassword ? 'text' : 'password'}
                      value={formData.password}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                        errors.password ? 'border-red-500' : 'border-[#CCCCCC] dark:border-[#444444]'
                      } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 pr-12 text-base font-normal leading-normal`}
                      placeholder="Enter your password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-0 top-0 h-12 px-4 text-muted-foreground hover:text-foreground transition-colors"
                      aria-label="Toggle password visibility"
                    >
                      {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-2">
                    8+ characters, one number, one uppercase letter
                  </p>
                  {errors.password && (
                    <p className="text-xs text-red-500 mt-1">{errors.password}</p>
                  )}
                </label>

                {/* Confirm Password */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Confirm Password *</p>
                  <div className="relative flex w-full flex-1 items-stretch">
                    <input
                      name="confirmPassword"
                      type={showConfirmPassword ? 'text' : 'password'}
                      value={formData.confirmPassword}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                        errors.confirmPassword ? 'border-red-500' : 'border-[#CCCCCC] dark:border-[#444444]'
                      } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 pr-12 text-base font-normal leading-normal`}
                      placeholder="Re-enter your password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      className="absolute right-0 top-0 h-12 px-4 text-muted-foreground hover:text-foreground transition-colors"
                      aria-label="Toggle confirm password visibility"
                    >
                      {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                  {errors.confirmPassword && (
                    <p className="text-xs text-red-500 mt-1">{errors.confirmPassword}</p>
                  )}
                </label>

                {/* Submit Button */}
                <div className="mt-6 flex flex-col gap-4">
                  <button 
                    type="submit" 
                    disabled={loading}
                    className="mt-4 w-full bg-blue-600 text-white p-3 rounded-lg font-bold hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed transition-colors flex items-center justify-center"
                  >
                    {loading ? (
                      <>
                        <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Creating Account...
                      </>
                    ) : (
                      'Create Company Account'
                    )}
                  </button>
                </div>

                {/* Login Link */}
                <div className="mt-8 text-center">
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    Already have an account?{' '}
                    <Link to="/company/login" className="font-bold text-[#00A6F2] hover:underline">
                      Log In
                    </Link>
                  </p>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CompanySignUp;
