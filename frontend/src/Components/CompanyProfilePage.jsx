import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import CompanyProfile from './Profile/CompanyProfile/Profile/CompanyProfile';

const CompanyProfilePage = () => {
  const navigate = useNavigate();
  const [companyId, setCompanyId] = useState(null);
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    const storedUserId = localStorage.getItem('userId');
    const storedCompanyId = localStorage.getItem('companyId');
    const userType = localStorage.getItem('userType');

    if (!storedUserId || userType !== 'COMPANY') {
      navigate('/company/login');
      return;
    }

    setUserId(parseInt(storedUserId));
    
    // If companyId not in localStorage, fetch it
    if (storedCompanyId) {
      setCompanyId(parseInt(storedCompanyId));
    } else {
      fetchCompanyId(storedUserId);
    }
  }, [navigate]);

  const fetchCompanyId = async (userId) => {
    try {
      const token = localStorage.getItem('authToken');
      const response = await fetch(`http://localhost:8080/api/company/user/${userId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setCompanyId(data.companyProfileId);
        localStorage.setItem('companyId', data.companyProfileId);
      }
    } catch (error) {
      console.error('Error fetching company ID:', error);
    }
  };

  if (!companyId || !userId) {
    return <div className="loading-message">Loading...</div>;
  }

  return <CompanyProfile companyId={companyId} userId={userId} />;
};

export default CompanyProfilePage;
