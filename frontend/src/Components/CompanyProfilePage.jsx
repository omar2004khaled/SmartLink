import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import CompanyNavbar from './CompanyNavbar';
import Navbar from './Navbar';
import CompanyProfile from './Profile/CompanyProfile/Profile/CompanyProfile';

const CompanyProfilePage = () => {
  const navigate = useNavigate();
  const { companyId: paramId } = useParams();
  const [userId, setUserId] = useState(null);
  const [loggedInUserType, setLoggedInUserType] = useState(null);

  useEffect(() => {
    const storedUserId = localStorage.getItem('userId');
    const userType = localStorage.getItem('userType');

    // Store the logged-in user's type
    setLoggedInUserType(userType);

    // If viewing own profile (no paramId), ensure logged in as company
    if (!paramId && (!storedUserId || userType !== 'COMPANY')) {
      navigate('/company/login');
      return;
    }

    if (storedUserId) {
      setUserId(parseInt(storedUserId));
    }
  }, [navigate, paramId]);

  if (!userId && !paramId) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: '#f5f5f5'
      }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{
            border: '4px solid #f3f3f3',
            borderTop: '4px solid #3498db',
            borderRadius: '50%',
            width: '40px',
            height: '40px',
            animation: 'spin 1s linear infinite',
            margin: '0 auto'
          }}></div>
          <style>{`
            @keyframes spin {
              0% { transform: rotate(0deg); }
              100% { transform: rotate(360deg); }
            }
          `}</style>
        </div>
      </div>
    );
  }

  return (
    <div>
      {loggedInUserType === 'COMPANY' ? <CompanyNavbar /> : <Navbar />}
      <CompanyProfile targetUserId={paramId} currentUserId={userId} />
    </div>
  );
};

export default CompanyProfilePage;
