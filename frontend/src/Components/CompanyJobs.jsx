import React, { useEffect, useState } from 'react';
import CompanyNavbar from './CompanyNavbar';
import CompanyJobsPage from '../JobPage/CompanyJobsPage';

const CompanyJobs = () => {
  const [companyId, setCompanyId] = useState(null);

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    if (userId) {
      setCompanyId(parseInt(userId));
    }
  }, []);

  return (
    <div>
      <CompanyNavbar />
      <div className="pt-20">
        <CompanyJobsPage companyId={companyId} />
      </div>
    </div>
  );
};

export default CompanyJobs;
