import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';

const VerifyEmail = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  
  const email = location.state?.email || '';
  const justRegistered = location.state?.justRegistered || false;

  useEffect(() => {
    if (!email) {
      navigate('/signup');
    }
  }, [email, navigate]);

  const handleResendVerification = async () => {
    setLoading(true);
    setError('');
    setMessage('');
    
    try {
      const response = await fetch("http://localhost:8080/auth/resend-verification", {
        method: "POST",
        headers: { 
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email })
      });

      const responseText = await response.text();
      let data;
      
      try {
        data = JSON.parse(responseText);
      } catch (e) {
        data = { message: responseText };
      }

      if (response.ok) {
        setMessage('✅ Verification email sent! Please check your inbox.');
      } else {
        setError(data.message || 'Failed to resend verification email. Please try again.');
      }
    } catch (error) {
      setError('Cannot connect to server. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2">
      {/* Left Panel - Same as your SignUp */}
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
            Verify Your Email
          </h1>
          <h2 className="text-gray-700 dark:text-gray-300 text-base font-normal leading-normal">
            Complete your registration by verifying your email address.
          </h2>
        </div>
        <div></div>
      </div>

      {/* Right Panel - Verification Content */}
      <div className="flex items-center justify-center p-6 sm:p-8 lg:p-12 bg-white dark:bg-[#1a1a1a]">
        <div className="w-full max-w-md">
          <div className="mb-8 text-left">
            <h1 className="text-3xl font-black leading-tight tracking-[-0.033em] text-[#333333] dark:text-[#F5F5F5]">
              Verify Your Email
            </h1>
            {justRegistered && (
              <div className="mt-2 p-3 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg">
                <p className="text-sm text-green-800 dark:text-green-200">
                  ✅ Registration successful! Please verify your email to continue.
                </p>
              </div>
            )}
          </div>

          <div className="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-6 mb-6">
            <div className="flex items-start gap-3">
              <div className="w-6 h-6 bg-blue-100 dark:bg-blue-800 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                <svg className="w-4 h-4 text-blue-600 dark:text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                </svg>
              </div>
              <div>
                <h3 className="font-semibold text-blue-900 dark:text-blue-100 mb-2">
                  Check Your Email
                </h3>
                <p className="text-blue-800 dark:text-blue-200 text-sm">
                  We've sent a verification link to:
                </p>
                <p className="font-medium text-blue-900 dark:text-blue-100 mt-1">
                  {email}
                </p>
                <p className="text-blue-800 dark:text-blue-200 text-sm mt-2">
                  Click the link in the email to verify your account and start using Smart Link.
                </p>
              </div>
            </div>
          </div>

          {/* Success/Error Messages */}
          {message && (
            <div className="p-4 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg mb-4">
              <p className="text-sm text-green-800 dark:text-green-200">{message}</p>
            </div>
          )}

          {error && (
            <div className="p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg mb-4">
              <p className="text-sm text-red-800 dark:text-red-300">{error}</p>
            </div>
          )}

          {/* Action Buttons */}
          <div className="space-y-4">
            <button
              onClick={handleResendVerification}
              disabled={loading}
              className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center"
            >
              {loading ? (
                <>
                  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Sending...
                </>
              ) : (
                'Resend Verification Email'
              )}
            </button>

            <div className="text-center pt-4 border-t border-gray-200 dark:border-gray-700">
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Need help?{' '}
                <Link to="/contact" className="font-semibold text-blue-600 hover:underline">
                  Contact Support
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VerifyEmail;