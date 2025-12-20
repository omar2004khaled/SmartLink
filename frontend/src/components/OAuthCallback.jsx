import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { API_BASE_URL } from '../config';

const OAuthCallback = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState('processing');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const handleOAuthCallback = async () => {
      try {
        const token = searchParams.get('token');
        console.log('OAuth callback received with token:', token);

        if (!token) {
          setStatus('error');
          setMessage('No authentication token received from Google.');
          return;
        }

        // 1. Store the token
        localStorage.setItem('authToken', token);

        // 2. Decode token to get email and userType
        const userData = parseJwt(token);
        console.log('User data from token:', userData);

        if (userData) {
          // Store basic info immediately
          if (userData.sub) localStorage.setItem('userEmail', userData.sub);
          if (userData.userType) localStorage.setItem('userType', userData.userType);

          // 3. Fetch full user details to get userID (DATABASE ID)
          try {
            const userResponse = await fetch(`${API_BASE_URL}/api/users/email/${userData.sub}`, {
              headers: {
                'Authorization': `Bearer ${token}`
              }
            });

            if (userResponse.ok) {
              const fullUserData = await userResponse.json();
              console.log("Fetched full user data:", fullUserData);
              localStorage.setItem('userId', fullUserData.id);
              localStorage.setItem('user', JSON.stringify(fullUserData));
            } else {
              console.warn("Failed to fetch user ID");
            }
          } catch (fetchErr) {
            console.error("Error fetching user details:", fetchErr);
          }
        }

        setStatus('success');
        setMessage('Google authentication successful! Redirecting...');


        setTimeout(() => {
          if (userData && userData.userType === 'COMPANY') {
            navigate('/company-profile');
          } else {
            navigate('/home');
          }
        }, 2000);

      } catch (error) {
        console.error('OAuth callback error:', error);
        setStatus('error');
        setMessage('Authentication failed. Please try again.');

        setTimeout(() => {
          navigate('/login');
        }, 3000);
      }
    };

    // Helper function to decode JWT
    const parseJwt = (token) => {
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
          atob(base64)
            .split('')
            .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
            .join('')
        );
        return JSON.parse(jsonPayload);
      } catch (error) {
        console.error('Error parsing JWT:', error);
        return null;
      }
    };

    handleOAuthCallback();
  }, [searchParams, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full bg-white rounded-lg shadow-md p-8 text-center">
        {status === 'processing' && (
          <>
            <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-blue-600 animate-spin" fill="none" viewBox="0 0 24 24">
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
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
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