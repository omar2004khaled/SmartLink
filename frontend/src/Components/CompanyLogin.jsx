import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';

const CompanyLogin = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState(location.state?.message || '');

  useEffect(() => {
    if (successMessage) {
      const timer = setTimeout(() => setSuccessMessage(''), 3000);
      return () => clearTimeout(timer);
    }
  }, [successMessage]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await fetch("http://localhost:8080/auth/company/login", {
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

      const responseText = await response.text();
      let data;
      
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        try {
          data = JSON.parse(responseText);
        } catch (parseError) {
          data = { message: responseText };
        }
      } else {
        data = { message: responseText };
      }

      if (!response.ok) {
        let errorMessage = 'Login failed. Please check your credentials.';
        
        if (data && typeof data.message === 'string') {
          errorMessage = data.message;
        } else if (responseText && typeof responseText === 'string') {
          errorMessage = responseText;
        } else if (data && data.error) {
          errorMessage = data.error;
        }

        if (errorMessage.includes('verify') || errorMessage.includes('verified')) {
          errorMessage = 'Please verify your email before logging in. Check your inbox for the verification link.';
        } else if (response.status === 401) {
          errorMessage = 'Invalid email or password. Please try again.';
        } else if (response.status === 403) {
          errorMessage = 'This account is not registered as a company. Please use job seeker login.';
        }

        setError(errorMessage);
        return;
      }

      if (data.token) {
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('userEmail', data.email);
        localStorage.setItem('userType', 'COMPANY');
        console.log("Company login successful, token stored");
        
        // Fetch userId
        try {
          const userResponse = await fetch(`http://localhost:8080/api/users/email/${data.email}`, {
            headers: {
              'Authorization': `Bearer ${data.token}`
            }
          });
          if (userResponse.ok) {
            const userData = await userResponse.json();
            localStorage.setItem('userId', userData.id);
          }
        } catch (err) {
          console.error('Failed to fetch user data:', err);
        }
      }

      // Get company profile ID
      try {
        const companyResponse = await fetch(`http://localhost:8080/api/company/user/${userData.id}`, {
          headers: {
            'Authorization': `Bearer ${data.token}`
          }
        });
        if (companyResponse.ok) {
          const companyData = await companyResponse.json();
          localStorage.setItem('companyId', companyData.companyProfileId);
        }
      } catch (err) {
        console.error('Failed to fetch company data:', err);
      }

      navigate('/company-profile');
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
            Welcome Back
          </h1>
          <h2 className="text-gray-700 dark:text-gray-300 text-base font-normal leading-normal">
            Access your company dashboard and connect with talent
          </h2>
        </div>
        <div></div>
      </div>

      {/* Right Panel - Login Form */}
      <div className="flex items-center justify-center p-6 sm:p-8 lg:p-12 bg-background">
        <div className="w-full max-w-md flex flex-col gap-8">
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-2 text-center md:text-left">
              <h1 className="text-3xl font-bold text-foreground">Company Login</h1>
              <p className="text-muted-foreground">
                Log in to access your company dashboard
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
                placeholder="company@example.com"
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

          {/* Sign up link */}
          <div className="text-center border-t border-border pt-6">
            <p className="text-sm text-muted-foreground">
              Don't have a company account?{' '}
              <Link 
                to="/company/signup" 
                className="font-semibold text-primary hover:underline"
              >
                Sign Up
              </Link>
            </p>
            <p className="text-sm text-muted-foreground mt-2">
              Looking for job seeker login?{' '}
              <Link 
                to="/login" 
                className="font-semibold text-primary hover:underline"
              >
                Click here
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CompanyLogin;
