import React, { useState } from 'react';

const SignUp = () => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    gender: '',
    birthDate: ''
  });

  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState(''); // Add this for API errors
  const [successMessage, setSuccessMessage] = useState(''); // Add this for success

  const validateField = (name, value) => {
    switch (name) {
      case 'fullName':
        if (!value.trim()) return 'Full name is required';
        if (value.trim().length < 2) return 'Name must be at least 2 characters';
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

      case 'gender':
        if (!value) return 'Gender is required';
        return '';

      case 'birthDate':
        if (!value) return 'Birth date is required';
        const today = new Date();
        const birth = new Date(value);
        const age = today.getFullYear() - birth.getFullYear();
        if (age < 13) return 'You must be at least 13 years old';
        if (age > 120) return 'Please enter a valid birth date';
        return '';

      default:
        return '';
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    // Clear API error when user starts typing
    if (apiError) setApiError('');
    if (successMessage) setSuccessMessage('');

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

  const registerUser = async () => {
    setLoading(true);
    setApiError(''); // Clear previous errors
    setSuccessMessage(''); // Clear previous success

    try {
      console.log("Sending registration request...");
      
      const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Accept": "application/json"
        },
        mode: 'cors',
        body: JSON.stringify({
          fullName: formData.fullName.trim(),
          email: formData.email.toLowerCase().trim(),
          password: formData.password,
          confirmPassword: formData.confirmPassword,
          phoneNumber: formData.phone.replace(/\D/g, ''),
          gender: formData.gender,
          birthDate: formData.birthDate
        })
      });

      console.log("Response status:", response.status);

      // Get response as text first
      const responseText = await response.text();
      console.log("Response text:", responseText);

      // Try to parse as JSON, fallback to plain text
      let data;
      try {
        data = JSON.parse(responseText);
      } catch (e) {
        data = { 
          message: responseText || 'No response from server',
          success: response.ok 
        };
      }

      console.log("Parsed data:", data);

      if (!response.ok) {
        // Extract error message
        let errorMessage = 'Registration failed';
        
        if (typeof data === 'string') {
          errorMessage = data;
        } else if (data.message) {
          errorMessage = data.message;
        } else if (data.error) {
          errorMessage = data.error;
        }

        // Handle specific error codes
        if (response.status === 409) {
          setApiError('This email is already registered. Please use a different email or try logging in.');
        } else if (response.status === 400) {
          setApiError(errorMessage);
        } else {
          setApiError(`${errorMessage} (Status: ${response.status})`);
        }
        
        return; // Don't throw, just return
      }

      // Success!
      console.log("Registration successful:", data);
      setSuccessMessage('Registration successful! Redirecting to login...');
      
      // Clear form
      setFormData({
        fullName: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        gender: '',
        birthDate: ''
      });

      // Redirect after 2 seconds
      setTimeout(() => {
        window.location.href = '/login';
      }, 2000);
      
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

    // Clear previous messages
    setApiError('');
    setSuccessMessage('');

    const newErrors = {};
    Object.keys(formData).forEach(key => {
      const error = validateField(key, formData[key]);
      if (error) newErrors[key] = error;
    });

    setErrors(newErrors);

    if (Object.keys(newErrors).length === 0) {
      registerUser();
    } else {
      // Scroll to top to see errors
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const PasswordToggleIcon = ({ show }) => (
    show ? (
      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
      </svg>
    ) : (
      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
      </svg>
    )
  );

  return (
    <div className="relative flex min-h-screen w-full flex-col overflow-x-hidden">
      <div className="flex-grow flex items-stretch">
        <div className="w-full grid grid-cols-1 md:grid-cols-2">
          {/* Left Panel */}
          <div className="hidden md:flex flex-col justify-between p-12 bg-[#FFEAEE] dark:bg-[#2C1A1D] text-center">
            <div className="self-start">
              <div className="flex items-center gap-2">
                <span className="text-2xl font-bold text-gray-800 dark:text-gray-200">Connect</span>
              </div>
            </div>
            <div className="flex flex-col gap-6">
              <h1 className="text-gray-800 dark:text-gray-200 text-5xl font-black leading-tight tracking-[-0.033em]">
                Connect. Collaborate. Grow.
              </h1>
              <h2 className="text-gray-700 dark:text-gray-300 text-base font-normal leading-normal">
                The premier platform for building your professional network and advancing your career.
              </h2>
            </div>
            <div></div>
          </div>

          {/* Right Panel - Form */}
          <div className="w-full flex items-center justify-center p-6 sm:p-8 lg:p-12 bg-white dark:bg-[#1a1a1a]">
            <div className="w-full max-w-md">
              <div className="mb-8 text-left">
                <h1 className="text-3xl font-black leading-tight tracking-[-0.033em] text-[#333333] dark:text-[#F5F5F5]">
                  Create Your Professional Account
                </h1>
              </div>

              {/* Success Message */}
              {successMessage && (
                <div className="mb-4 p-4 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg flex items-start gap-3">
                  <svg className="w-5 h-5 text-green-600 dark:text-green-400 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <div className="flex-1">
                    <p className="text-sm font-medium text-green-800 dark:text-green-200">{successMessage}</p>
                  </div>
                </div>
              )}

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
                {/* Full Name */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Full Name</p>
                  <input
                    name="fullName"
                    value={formData.fullName}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.fullName 
                        ? 'border-red-500'
                        : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="e.g. Jane Doe"
                  />
                  {errors.fullName && (
                    <p className="text-xs text-red-500 mt-1">{errors.fullName}</p>
                  )}
                </label>

                {/* Work Email */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Work Email</p>
                  <input
                    name="email"
                    type="email"
                    value={formData.email}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.email 
                        ? 'border-red-500'
                        : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="name@company.com"
                  />
                  {errors.email && (
                    <p className="text-xs text-red-500 mt-1">{errors.email}</p>
                  )}
                </label>

                {/* Phone Number */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Phone Number</p>
                  <input
                    name="phone"
                    type="tel"
                    value={formData.phone}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.phone 
                        ? 'border-red-500'
                        : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="(123) 456-7890"
                  />
                  {errors.phone && (
                    <p className="text-xs text-red-500 mt-1">{errors.phone}</p>
                  )}
                </label>

                {/* Gender */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Gender</p>
                  <select
                    name="gender"
                    value={formData.gender}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    className={`form-select flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.gender 
                        ? 'border-red-500'
                        : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 px-4 text-base font-normal leading-normal`}
                  >
                    <option value="">Select gender</option>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                  </select>
                  {errors.gender && (
                    <p className="text-xs text-red-500 mt-1">{errors.gender}</p>
                  )}
                </label>

                {/* Birth Date */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Birth Date</p>
                  <input
                    name="birthDate"
                    type="date"
                    value={formData.birthDate}
                    onChange={handleChange}
                    onBlur={handleBlur}
                    max={new Date(new Date().setFullYear(new Date().getFullYear() - 13)).toISOString().split('T')[0]}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.birthDate 
                        ? 'border-red-500'
                        : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                  />
                  {errors.birthDate && (
                    <p className="text-xs text-red-500 mt-1">{errors.birthDate}</p>
                  )}
                </label>

                {/* Password */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Password</p>
                  <div className="relative flex w-full flex-1 items-stretch">
                    <input
                      name="password"
                      type={showPassword ? 'text' : 'password'}
                      value={formData.password}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                        errors.password 
                          ? 'border-red-500'
                          : 'border-[#CCCCCC] dark:border-[#444444]'
                      } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 pr-12 text-base font-normal leading-normal`}
                      placeholder="Enter your password"
                    />
                    <div
                      className="absolute inset-y-0 right-0 flex items-center pr-3 cursor-pointer text-gray-400 hover:text-gray-600"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      <PasswordToggleIcon show={showPassword} />
                    </div>
                  </div>
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-2">
                    8+ characters, one number, one uppercase letter.
                  </p>
                  {errors.password && (
                    <p className="text-xs text-red-500 mt-1">{errors.password}</p>
                  )}
                </label>

                {/* Confirm Password */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Confirm Password</p>
                  <div className="relative flex w-full flex-1 items-stretch">
                    <input
                      name="confirmPassword"
                      type={showConfirmPassword ? 'text' : 'password'}
                      value={formData.confirmPassword}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                        errors.confirmPassword 
                          ? 'border-red-500'
                          : 'border-[#CCCCCC] dark:border-[#444444]'
                      } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 pr-12 text-base font-normal leading-normal`}
                      placeholder="Re-enter your password"
                    />
                    <div
                      className="absolute inset-y-0 right-0 flex items-center pr-3 cursor-pointer text-gray-400 hover:text-gray-600"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    >
                      <PasswordToggleIcon show={showConfirmPassword} />
                    </div>
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
                        Signing Up...
                      </>
                    ) : (
                      'Sign Up'
                    )}
                  </button>
                  
                  <p className="text-center text-xs text-gray-500 dark:text-gray-400">
                    By signing up, you agree to our{' '}
                    <a className="font-medium text-[#00A6F2] hover:underline" href="#">
                      Terms of Service
                    </a>{' '}
                    and{' '}
                    <a className="font-medium text-[#00A6F2] hover:underline" href="#">
                      Privacy Policy
                    </a>
                    .
                  </p>
                </div>

                {/* Divider */}
                <div className="flex items-center my-6">
                  <div className="flex-grow border-t border-[#CCCCCC] dark:border-[#444444]"></div>
                  <span className="flex-shrink mx-4 text-xs text-gray-400 dark:text-gray-500">OR</span>
                  <div className="flex-grow border-t border-[#CCCCCC] dark:border-[#444444]"></div>
                </div>

                {/* Google Sign Up */}
                <button
                  type="button"
                  className="flex w-full items-center justify-center gap-2 h-12 px-4 rounded-lg border border-[#CCCCCC] dark:border-[#444444] hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
                >
                  <svg className="h-5 w-5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path
                      d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                      fill="#4285F4"
                    ></path>
                    <path
                      d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                      fill="#34A853"
                    ></path>
                    <path
                      d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z"
                      fill="#FBBC05"
                    ></path>
                    <path
                      d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                      fill="#EA4335"
                    ></path>
                  </svg>
                  <span className="text-sm font-medium">Sign up with Google</span>
                </button>

                {/* Login Link */}
                <div className="mt-8 text-center">
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    Already have an account?{' '}
                    <a className="font-bold text-[#00A6F2] hover:underline" href="/login">
                      Log In
                    </a>
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

export default SignUp;