import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';
import { useLocation } from "react-router-dom";
import { useEffect } from "react";
import { API_BASE_URL, GOOGLE_OAUTH2_AUTH_URL } from '../config';


const Login = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const location = useLocation();

  const [successMessage, setSuccessMessage] = useState(location.state?.message || '');

  const handleGoogleLogin = () => {
   window.location.href = GOOGLE_OAUTH2_AUTH_URL;
};


useEffect(() => {
  const params = new URLSearchParams(location.search);
  const errorParam = params.get("error");

  if (errorParam === "not_registered") {
    setError("This Google account is not registered. Please sign up first.");
    setSuccessMessage('');

    const cleanUrl = window.location.pathname;
    window.history.replaceState({}, "", cleanUrl);
  }
}, [location]);
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  useEffect(() => {
  if (successMessage) {
    const timer = setTimeout(() => setSuccessMessage(''), 3000);
    return () => clearTimeout(timer);
  }
}, [successMessage]);

 const handleSubmit = async (e) => {
  e.preventDefault();
  setLoading(true);
  setError('');

  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: { 
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      mode: 'cors',
      body: JSON.stringify({
        email: formData.email.toLowerCase().trim(),
        password: formData.password
      })
    });

    console.log("Response status:", response.status);
    console.log("Content-Type:", response.headers.get('content-type'));

    // Get response as text first
    const responseText = await response.text();
    console.log("Raw response:", responseText);

    let data;
    
    // Check if response is JSON
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      try {
        data = JSON.parse(responseText);
      } catch (parseError) {
        console.error("JSON parse error:", parseError);
        // If JSON parsing fails but we have text, use the text as message
        data = { message: responseText };
      }
    } else {
      // Response is not JSON, use the text as the error message
      data = { message: responseText };
    }

    console.log("Processed data:", data);

    if (!response.ok) {
      // Extract error message safely
      let errorMessage = 'Login failed. Please check your credentials.';
      
      if (data && typeof data.message === 'string') {
        errorMessage = data.message;
      } else if (responseText && typeof responseText === 'string') {
        errorMessage = responseText;
      } else if (data && data.error) {
        errorMessage = data.error;
      }

      // Handle specific error cases
      if (errorMessage.includes('verify') || errorMessage.includes('verified')) {
        errorMessage = 'Please verify your email before logging in. Check your inbox for the verification link.';
      } else if (response.status === 401) {
        errorMessage = 'Invalid email or password. Please try again.';
      } else if (response.status === 403) {
        errorMessage = 'Account not verified. Please check your email for verification link.';
      }

      setError(errorMessage);
      return;
    }

    // Success case - response should be JSON
    if (data.token) {
      localStorage.setItem('authToken', data.token);
      console.log("Login successful, token stored");
    }

    navigate('/dashboard'); 
  } catch (error) {
    console.error("Login Error:", error);
    if (error.message === 'Failed to fetch') {
      setError('Cannot connect to server. Please try again later.');
    } else {
      setError('An unexpected error occurred. Please try again.');
    }
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2">
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
                Connect. Collaborate. Grow.
              </h1>
              <h2 className="text-gray-700 dark:text-gray-300 text-base font-normal leading-normal">
                The premier platform for building your professional network and advancing your career.
              </h2>
            </div>
            <div></div>
          </div>

      {/* Right Panel - Login Form */}
      <div className="flex items-center justify-center p-6 sm:p-8 lg:p-12 bg-background">
        <div className="w-full max-w-md flex flex-col gap-8">
          <div className="flex flex-col gap-4">
            <div className="flex items-center gap-2 md:justify-start justify-center">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                <svg 
                  className="w-5 h-5 text-primary"
                  fill="none" 
                  stroke="currentColor" 
                  strokeWidth="2.5" 
                  viewBox="0 0 48 48"
                >
                  <path 
                    d="M24 6 C14.058 6 6 14.058 6 24 C 6 33.942 14.058 42 24 42 C 33.942 42 42 33.942 42 24" 
                    strokeLinecap="round" 
                    strokeLinejoin="round"
                  />
                  <path 
                    d="M30 18 L18 30" 
                    strokeLinecap="round" 
                    strokeLinejoin="round"
                  />
                  <path 
                    d="M18 18 H30 V30" 
                    strokeLinecap="round" 
                    strokeLinejoin="round"
                  />
                </svg>
              </div>
             
            </div>

            <div className="flex flex-col gap-2 text-center md:text-left">
              <h1 className="text-3xl font-bold text-foreground">Welcome Back</h1>
              <p className="text-muted-foreground">
                Log in to access your professional network.
              </p>
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
                <p className="text-sm text-red-700">{error}</p>
            </div>
            )}

            {/* Success Message */}
            {successMessage && (
            <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
                <p className="text-sm text-green-700">{successMessage}</p>
            </div>
            )}

          {/* Login Form */}
          <form onSubmit={handleSubmit} className="flex flex-col gap-5">
            {/* Email */}
            <label className="flex flex-col gap-2">
              <span className="text-sm font-medium text-foreground">Email Address</span>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                className="h-12 px-4 rounded-lg border border-input bg-muted/30 focus:bg-background focus:border-ring focus:ring-2 focus:ring-ring/20 text-foreground placeholder:text-muted-foreground transition-colors"
                placeholder="you@example.com"
              />
            </label>

            {/* Password */}
            <label className="flex flex-col gap-2">
              <span className="text-sm font-medium text-foreground">Password</span>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  required
                  className="h-12 w-full px-4 pr-12 rounded-lg border border-input bg-muted/30 focus:bg-background focus:border-ring focus:ring-2 focus:ring-ring/20 text-foreground placeholder:text-muted-foreground transition-colors"
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
            </label>

            {/* Submit Button */}
            <div className="flex flex-col gap-4 mt-2">
            <button
            type="submit"
            disabled={loading}
            className="h-12 w-full bg-primary hover:bg-primary/90 text-white font-semibold rounded-lg transition-colors disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center"
            >
            {loading ? (
                <>
                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
                Logging in...
                </>
            ) : (
                'Log In'
            )}
            </button>

              <div className="text-center">
                <Link 
                  to="/forgot-password"
                  className="text-sm font-medium text-primary hover:underline"
                >
                  Forgot Password?
                </Link>
              </div>
            </div>
          </form>

          {/* Divider */}
          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-border" />
            </div>
            <div className="relative flex justify-center text-xs uppercase">
              <span className="bg-background px-2 text-muted-foreground">or</span>
            </div>
          </div>

          {/* Google login */}
                <button
                  type="button"
                 onClick={handleGoogleLogin}
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
                  <span className="text-sm font-medium">Continue with Google</span>
                </button>


          {/* Sign up link */}
          <div className="text-center border-t border-border pt-6">
            <p className="text-sm text-muted-foreground">
              Don't have an account?{' '}
              <Link 
                to="/signup" 
                className="font-semibold text-primary hover:underline"
              >
                Sign Up
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;