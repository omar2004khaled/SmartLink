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
    return <div className="loading-message">Loading...</div>;
  }

  return (
    <div>
      {loggedInUserType === 'COMPANY' ? <CompanyNavbar /> : <Navbar />}
      <CompanyProfile targetUserId={paramId} currentUserId={userId} />
    </div>
  );
};

export default CompanyProfilePage;
