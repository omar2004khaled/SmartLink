import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import CompanyNavbar from './CompanyNavbar';
import CompanyProfile from './Profile/CompanyProfile/Profile/CompanyProfile';

const CompanyProfilePage = () => {
  const navigate = useNavigate();
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    const storedUserId = localStorage.getItem('userId');
    const userType = localStorage.getItem('userType');

    if (!storedUserId || userType !== 'COMPANY') {
      navigate('/company/login');
      return;
    }

    setUserId(parseInt(storedUserId));
  }, [navigate]);

  if (!userId) {
    return <div className="loading-message">Loading...</div>;
  }

  return (
    <div>
      <CompanyNavbar />
      <CompanyProfile userId={userId} />
    </div>
  );
};

export default CompanyProfilePage;
