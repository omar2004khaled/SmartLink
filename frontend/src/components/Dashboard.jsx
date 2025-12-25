import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

const Dashboard = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get('token');
    
    if (token) {
      // Store the token and user data
      localStorage.setItem('authToken', token);
      
      // Decode token to get user info
      const userData = parseJwt(token);
      if (userData) {
        localStorage.setItem('user', JSON.stringify(userData));
      }
      
      //console.log('OAuth user logged in successfully');
    }
    
    // You can redirect to a different page if needed
  }, [searchParams, navigate]);

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

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full bg-white rounded-lg shadow-md p-8 text-center">
        <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <h1 className="text-3xl font-bold text-gray-800 mb-4">Welcome to Dashboard!</h1>
        <p className="text-gray-600 mb-6">You have successfully signed in.</p>
        <div className="space-y-3">
          <button 
            onClick={() => navigate('/profile')}
            className="w-full bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
          >
            View Profile
          </button>
          <button 
            onClick={() => navigate('/')}
            className="w-full bg-gray-200 text-gray-800 px-4 py-2 rounded-lg hover:bg-gray-300"
          >
            Go Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
