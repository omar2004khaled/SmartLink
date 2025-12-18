import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { Eye, EyeOff, Lock } from 'lucide-react';
import { API_BASE_URL } from '../config';

const ResetPassword = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  const [formData, setFormData] = useState({
    password: '',
    confirmPassword: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [tokenValid, setTokenValid] = useState(true);

  useEffect(() => {
    if (!token) {
      setTokenValid(false);
      setError('Invalid or missing reset token.');
    }
  }, [token]);

  const validatePassword = (password) => {
    if (password.length < 8) {
      return 'Password must be at least 8 characters long';
    }
    if (!/[A-Z]/.test(password)) {
      return 'Password must contain at least one uppercase letter';
    }
    if (!/[a-z]/.test(password)) {
      return 'Password must contain at least one lowercase letter';
    }
    if (!/[0-9]/.test(password)) {
      return 'Password must contain at least one number';
    }
    return null;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (error) setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validation
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    const passwordError = validatePassword(formData.password);
    if (passwordError) {
      setError(passwordError);
      return;
    }

    setLoading(true);

    try {
      const response = await fetch(`${API_BASE_URL}/auth/reset-password`, {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Accept": "application/json"
        },
        mode: 'cors',
        body: JSON.stringify({
          token: token,
          newPassword: formData.password
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
        let errorMessage = 'Failed to reset password. Please try again.';
        
        if (data && typeof data.message === 'string') {
          errorMessage = data.message;
        } else if (responseText && typeof responseText === 'string') {
          errorMessage = responseText;
        }

        if (response.status === 400 || errorMessage.includes('expired') || errorMessage.includes('invalid')) {
          setTokenValid(false);
        }

        setError(errorMessage);
        return;
      }

      // Success - redirect to login
      navigate('/login', { 
        state: { message: 'Password reset successfully! Please log in with your new password.' }
      });
    } catch (error) {
      console.error("Reset Password Error:", error);
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
            Create New Password
          </h1>
          <h2 className="text-gray-700 dark:text-gray-300 text-base font-normal leading-normal">
            Choose a strong password to secure your account.
          </h2>
        </div>
        <div></div>
      </div>

      {/* Right Panel */}
      <div className="flex items-center justify-center p-6 sm:p-8 lg:p-12 bg-background">
        <div className="w-full max-w-md flex flex-col gap-8">
          {tokenValid ? (
            <>
              <div className="flex flex-col gap-2 text-center md:text-left">
                <h1 className="text-3xl font-bold text-foreground">Reset Password</h1>
                <p className="text-muted-foreground">
                  Enter your new password below.
                </p>
              </div>

              {error && (
                <div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg">
                  <p className="text-sm text-destructive">{error}</p>
                </div>
              )}

              <div className="flex flex-col gap-5">
                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium text-foreground">New Password</span>
                  <div className="relative">
                    <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-muted-foreground" size={20} />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                      required
                      className="h-12 w-full pl-12 pr-12 rounded-lg border border-input bg-muted/30 focus:bg-background focus:border-ring focus:ring-2 focus:ring-ring/20 text-foreground placeholder:text-muted-foreground transition-colors"
                      placeholder="Enter new password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-0 top-0 h-12 px-4 text-muted-foreground hover:text-foreground transition-colors"
                    >
                      {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                </label>

                <label className="flex flex-col gap-2">
                  <span className="text-sm font-medium text-foreground">Confirm Password</span>
                  <div className="relative">
                    <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-muted-foreground" size={20} />
                    <input
                      type={showConfirmPassword ? 'text' : 'password'}
                      name="confirmPassword"
                      value={formData.confirmPassword}
                      onChange={handleChange}
                      required
                      className="h-12 w-full pl-12 pr-12 rounded-lg border border-input bg-muted/30 focus:bg-background focus:border-ring focus:ring-2 focus:ring-ring/20 text-foreground placeholder:text-muted-foreground transition-colors"
                      placeholder="Confirm new password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      className="absolute right-0 top-0 h-12 px-4 text-muted-foreground hover:text-foreground transition-colors"
                    >
                      {showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                </label>

                <div className="p-3 bg-muted/50 rounded-lg text-xs text-muted-foreground">
                  <p className="font-medium mb-1">Password requirements:</p>
                  <ul className="list-disc list-inside space-y-0.5">
                    <li>At least 8 characters long</li>
                    <li>Contains uppercase and lowercase letters</li>
                    <li>Contains at least one number</li>
                  </ul>
                </div>

                <button
                  onClick={handleSubmit}
                  disabled={loading}
                  className="h-12 w-full bg-primary hover:bg-primary/90 text-white font-semibold rounded-lg transition-colors disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center"
                >
                  {loading ? (
                    <>
                      <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                      </svg>
                      Resetting...
                    </>
                  ) : (
                    'Reset Password'
                  )}
                </button>
              </div>
            </>
          ) : (
            <div className="flex flex-col gap-6 text-center">
              <div className="w-16 h-16 rounded-full bg-destructive/10 flex items-center justify-center mx-auto">
                <svg className="w-8 h-8 text-destructive" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </div>
              
              <div className="flex flex-col gap-2">
                <h2 className="text-2xl font-bold text-foreground">Invalid or Expired Link</h2>
                <p className="text-muted-foreground">
                  This password reset link is invalid or has expired.
                </p>
              </div>

              <div className="p-4 bg-muted/50 rounded-lg text-sm text-muted-foreground text-left">
                <p>Reset links expire after 1 hour for security reasons. Please request a new password reset link.</p>
              </div>

              <Link
                to="/forgot-password"
                className="h-12 w-full flex items-center justify-center bg-primary hover:bg-primary/90 text-white font-semibold rounded-lg transition-colors"
              >
                Request New Link
              </Link>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ResetPassword;