import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { setAuthToken, setUserInfo, getUserInfo } from '../services/services';

const OAuthCallback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState('processing');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const handleOAuthCallback = async () => {
      try {
        const token = searchParams.get('token');
        const error = searchParams.get('error');

        console.log('OAuth callback - token:', token);
        console.log('OAuth callback - error:', error);

        // Handle error cases
        if (error) {
          setStatus('error');
          if (error === 'not_registered') {
            setMessage('This Google account is not registered. Please sign up first.');
          } else {
            setMessage('Google authentication failed. Please try again.');
          }
          
          setTimeout(() => {
            navigate('/login', {
              state: { 
                error: error === 'not_registered' 
                  ? 'This Google account is not registered. Please sign up first.'
                  : 'Google authentication failed. Please try again.'
              }
            });
          }, 2000);
          return;
        }

        // Handle missing token
        if (!token) {
          setStatus('error');
          setMessage('No authentication token received from Google.');
          
          setTimeout(() => {
            navigate('/login', {
              state: { error: 'Authentication failed. Please try again.' }
            });
          }, 2000);
          return;
        }

        // Store the token using auth service
        setAuthToken(token);
        console.log('Token stored in localStorage');

        // Fetch user info using the token
        try {
          const userInfo = await getUserInfo(token);
          console.log('User info fetched:', userInfo);
          
          setStatus('success');
          setMessage('Google authentication successful! Setting up your account...');

          // Redirect to create-posts for new Google users
          setTimeout(() => {
            navigate('/create-posts', {
              replace: true,
              state: {
                message: 'Welcome! Your account has been created successfully with Google.',
                isNewGoogleUser: true
              }
            });
          }, 1500);

        } catch (userInfoError) {
          console.error('Failed to fetch user info:', userInfoError);
          // Even if user info fails, token is stored, so redirect to dashboard
          setStatus('success');
          setMessage('Signed in successfully! Redirecting...');
          
          setTimeout(() => {
            navigate('/create-posts', { replace: true });
          }, 1500);
        }

      } catch (error) {
        console.error('OAuth callback error:', error);
        setStatus('error');
        setMessage('Authentication failed. Please try again.');
        
        setTimeout(() => {
          navigate('/login', {
            state: { error: 'An unexpected error occurred during authentication.' }
          });
        }, 2000);
      }
    };

    handleOAuthCallback();
  }, [searchParams, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 via-white to-red-50">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8 text-center">
        {status === 'processing' && (
          <>
            <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-primary animate-spin" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">Completing Authentication</h2>
            <p className="text-gray-600">Please wait while we sign you in with Google...</p>
          </>
        )}

        {status === 'success' && (
          <>
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">Success!</h2>
            <p className="text-gray-600 mb-4">{message}</p>
          </>
        )}

        {status === 'error' && (
          <>
            <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">Authentication Failed</h2>
            <p className="text-gray-600 mb-4">{message}</p>
            <button 
              onClick={() => navigate('/login')}
              className="bg-primary text-white px-6 py-2 rounded-lg hover:bg-primary/90 transition-colors"
            >
              Back to Login
            </button>
          </>
        )}
      </div>
    </div>
  );
};

export default OAuthCallback;