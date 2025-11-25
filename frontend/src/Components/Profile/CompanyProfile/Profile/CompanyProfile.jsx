import React, { useState, useEffect } from 'react';
import CompanyLogo from '../Logos/CompanyLogo';
import CompanyHeader from '../Header/CompanyHeader';
import ActionButtons from '../Buttons/ActionButtons';
import NavigationTabs from '../Tabs/NavigationTabs';
import OverviewSection from '../Tabs/OverviewSection';
import LocationsSection from '../LocationSection/LocationsSection';
import PostsTab from '../Tabs/PostsTab';
import './CompanyProfile.css';

export default function CompanyProfile({ companyId,userId }) {
  const [activeTab, setActiveTab] = useState('About');
  const [companyData, setCompanyData] = useState(null);
  const [tabData, setTabData] = useState(null);
  const [isFollowing, setIsFollowing] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const tabs = ['About', 'Posts'];

  useEffect(() => {
    const fetchData = async () => {
      await fetchCompanyBasicData();
      await fetchTabData('About');
    };
    
    fetchData();
  }, [companyId]);

  const fetchCompanyBasicData = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/company/${companyId}`);
      if (!response.ok) throw new Error('Failed to fetch company data');
      const data = await response.json();
      setCompanyData(data);
      setIsFollowing(data.isFollowing || false);
    } catch (err) {
      setError(err.message);
      console.error('Error fetchCompanyBasicData:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = async (tab) => {
    setActiveTab(tab);
    await fetchTabData(tab);
  };

  const fetchTabData = async (tab) => {
    try {
      const endpoint = tab === 'About' 
        ? `/api/company/${companyId}/about` 
        : `/api/company/${companyId}/posts`;
      
      const response = await fetch(endpoint);
      if (!response.ok) throw new Error(`Failed to fetch ${tab} data`);
      
      const data = await response.json();
      setTabData(data);
    } catch (err) {
       console.error(`Error get ${tab}:`, err);
      setError(err.message);
    }
  };

  const handleFollow = async () => {
    try{
      const op=isFollowing?"unfollow":"follow";
      const response = await fetch(`/api/company/${companyId}/${op}`,{
        method:"POST",
        headers:{
          'Content-Type': 'application/json',
        },
        body:JSON.stringify({userId:userId}),
      });
      if(!response.ok) throw new Error(`failed in ${op}`);
      setIsFollowing(!isFollowing);
      if(op==="follow"){
        companyData.numberOfFollowers +=1 
      }else{
        companyData.numberOfFollowers -=1; 
      }
      

    } catch(err){
      setError(err.message);
      console.error('Error handleFollow:', err);
    }

  };

  const handleVisitWebsite = () => {
    if (companyData?.website) {
      window.open(companyData.website, '_blank', 'noopener,noreferrer');
    }
  };
  if (loading) {
    return (
      <div className="company-profile-container">
        <div className="loading-message">Loading company profile...</div>
      </div>
    );
  }

  // Error state
  if (error && !companyData) {
    return (
      <div className="company-profile-container">
        <div className="error-message">Error: {error}</div>
      </div>
    );
  }

  // Not found state
  if (!companyData) {
    return (
      <div className="company-profile-container">
        <div className="error-message">Company not found</div>
      </div>
    );
  }

  return (
    <div className="company-profile-container">
      <div className="company-profile-header">
        <CompanyLogo 
          logoUrl={companyData.logoUrl} 
          coverUrl={companyData.coverUrl}
          companyName={companyData.companyName} 
        />
        
        <div className="company-info-wrapper">
          <CompanyHeader
            companyName={companyData.companyName}
            description={companyData.description}
            industry={companyData.industry}
            location={companyData.location}
            followers={companyData.numberOfFollowers}
          />
          
          <ActionButtons
            onFollow={handleFollow}
            onVisitWebsite={handleVisitWebsite}
            isFollowing={isFollowing}
          />
        </div>
      </div>

      <NavigationTabs 
        activeTab={activeTab} 
        onTabChange={handleTabChange} 
        tabs={tabs}
      />

      <div className="company-profile-content">
        {activeTab === 'About' && tabData && (
          <>
            <OverviewSection
              description={tabData.description}
              website={tabData.website}
              industry={tabData.industry}
              founded={tabData.founded}
            />
            <LocationsSection locations={tabData.locations || []} />
          </>
        )}
        
        {activeTab === 'Posts' && tabData && (
          <PostsTab posts={tabData.posts || []} />
        )}
        
        {!tabData && (
          <div className="loading-message">Loading {activeTab.toLowerCase()} data...</div>
        )}
      </div>
    </div>
  );
}