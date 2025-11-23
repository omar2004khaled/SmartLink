import React, { useState, useEffect } from 'react';
import CompanyLogo from './CompanyLogo';
import CompanyHeader from './CompanyHeader';
import ActionButtons from './ActionButtons';
import NavigationTabs from './NavigationTabs';
import OverviewSection from './OverviewSection';
import LocationsSection from './LocationsSection';
import PostsTab from './PostsTab';

export default function CompanyProfile({ companyId,userId }) {
  const [activeTab, setActiveTab] = useState('About');
  const [companyData, setCompanyData] = useState(null);
  const [tabData, setTabData] = useState(null);
  const [isFollowing, setIsFollowing] = useState(false);
  const [error, setError] = useState(null);
  const tabs = ['About', 'Posts'];

  useEffect(() => {
    fetchCompanyBasicData();
  }, [companyId]);

  const fetchCompanyBasicData = async () => {
    try {

      const response = await fetch(`/api/company/${companyId}`);
      if (!response.ok) throw new Error('Failed to fetch company data');
      const data = await response.json();
      setCompanyData(data);
      setIsFollowing(data.isFollowing || false);
    } catch (err) {
      setError(err.message);
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

    } catch(err){
        setError(err.message);

    }
   
  };

  const handleVisitWebsite = () => {
      window.open(companyData.website, '_blank');  
  };


  

  if(error) return <div>error: {error}</div>

  return (
    <div>
      <div>
        <CompanyLogo logoUrl={companyData.logoUrl} companyName={companyData.companyName} />
        
        <div>
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

      <div>
       
        
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
      </div>
    </div>
  );
}