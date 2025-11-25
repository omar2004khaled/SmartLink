import React, { useState } from 'react';

const SignUp = () => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: ''
  });

  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);


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

      default:
        return '';
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

  };


  const handleSubmit = (e) => {
    e.preventDefault();

    // Validate all fields
    const newErrors = {};
    Object.keys(formData).forEach(key => {
      const error = validateField(key, formData[key]);
      if (error) newErrors[key] = error;
    });

    setErrors(newErrors);


    if (Object.keys(newErrors).length === 0) {
      console.log('Form is valid, ready to submit:', formData);
      // Will integrate API in next push
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

              <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                {/* Full Name */}
                <label className="flex flex-col w-full">
                  <p className="text-sm font-medium leading-normal pb-2">Full Name</p>
                  <input
                    name="fullName"
                    value={formData.fullName}
                    onChange={handleChange}
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.fullName && touched.fullName
                        ? 'border-red-500'
                        : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="e.g. Jane Doe"
                  />
                  {errors.fullName  && (
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
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.email 
                        ? 'border-red-500'
                        : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="name@company.com"
                  />
                  {errors.email  && (
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
                    className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                      errors.phone 
                        ? 'border-red-500'
                        : 'border-[#CCCCCC] dark:border-[#444444]'
                    } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 text-base font-normal leading-normal`}
                    placeholder="(123) 456-7890"
                  />
                  {errors.phone && touched.phone && (
                    <p className="text-xs text-red-500 mt-1">{errors.phone}</p>
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
                      
                      className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                        errors.password 
                          ? 'border-red-500'
                          : 'border-[#CCCCCC] dark:border-[#444444]'
                      } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 pr-12 text-base font-normal leading-normal`}
                      placeholder="Enter your password"
                    />
                    <div
                      className="absolute inset-y-0 right-0 flex items-center pr-3 cursor-pointer text-[#CCCCCC] dark:text-[#444444]"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      <img
                        src={showPassword ? "/src/icons/view.png" : "/src/icons/hide.png"}
                        alt="toggle password visibility"
                        className="w-5 h-5"
                    />
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
                      className={`form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-[#333333] dark:text-[#F5F5F5] focus:outline-0 focus:ring-2 focus:ring-[#00A6F2]/50 border ${
                        errors.confirmPassword 
                          ? 'border-red-500'
                          : 'border-[#CCCCCC] dark:border-[#444444]'
                      } bg-white dark:bg-[#1a1a1a] focus:border-[#00A6F2] h-12 placeholder:text-[#CCCCCC] dark:placeholder:text-[#444444] px-4 pr-12 text-base font-normal leading-normal`}
                      placeholder="Re-enter your password"
                    />
                    <div
                      className="absolute inset-y-0 right-0 flex items-center pr-3 cursor-pointer text-[#CCCCCC] dark:text-[#444444]"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    >
                    <img
                        src={showConfirmPassword ? "/src/icons/view.png" : "/src/icons/hide.png"}
                        alt="toggle password visibility"
                        className="w-5 h-5"
                    />
                    </div>
                  </div>
                  {errors.confirmPassword  && (
                    <p className="text-xs text-red-500 mt-1">{errors.confirmPassword}</p>
                  )}
                </label>

                {/* Submit Button */}
                <div className="mt-6 flex flex-col gap-4">
                  <button
                    type="submit"
                    className="flex w-full cursor-pointer items-center justify-center overflow-hidden rounded-lg h-12 px-5 bg-[#00A6F2] text-white text-base font-bold leading-normal tracking-[0.015em] hover:bg-[#00A6F2]/90 focus:outline-none focus:ring-2 focus:ring-[#00A6F2] focus:ring-offset-2 dark:focus:ring-offset-[#1a1a1a]"
                  >
                    <span className="truncate">Sign Up</span>
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
                  className="flex w-full items-center justify-center gap-2 h-12 px-4 rounded-lg border border-[#CCCCCC] dark:border-[#444444] hover:bg-gray-50 dark:hover:bg-gray-800"
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