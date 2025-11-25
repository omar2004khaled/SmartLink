import React, { useState, useEffect } from 'react';
import CompanyLogo from '../Logos/CompanyLogo';
import CompanyHeader from '../Header/CompanyHeader';
import ActionButtons from '../Buttons/ActionButtons';
import NavigationTabs from '../Tabs/NavigationTabs';
import OverviewSection from '../Tabs/OverviewSection';
import LocationsSection from '../LocationSection/LocationsSection';
import PostsTab from '../Tabs/PostsTab';
import EditModal from '../EditModal/EditModal';
import './CompanyProfile.css';

export default function CompanyProfile({ companyId, userId }) {
  const [activeTab, setActiveTab] = useState('About');
  const [companyData, setCompanyData] = useState(null);
  const [tabData, setTabData] = useState(null);
  const [isFollowing, setIsFollowing] = useState(false);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isOwner, setIsOwner] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editSection, setEditSection] = useState(null);
  const tabs = ['About', 'Posts'];

  useEffect(() => {
    const fetchData = async () => {
      await fetchCompanyBasicData();
    };

    fetchData();
  }, [companyId, userId]);

  const fetchCompanyBasicData = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/company/${companyId}`);
      if (!response.ok) throw new Error('Failed to fetch company data');
      const data = await response.json();
      setCompanyData(data);
      setIsFollowing(data.isFollowing || false);
      setIsOwner(data.ownerId === userId);
      
      setTabData({
        description: data.description,
        website: data.website,
        industry: data.industry,
        founded: data.founded,
        locations: data.locations || []
      });
    } catch (err) {
      setError(err.message);
      console.error('Error fetchCompanyBasicData:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = async (tab) => {
    setActiveTab(tab);
    
    if (tab === 'About') {
      setTabData({
        description: companyData.description,
        website: companyData.website,
        industry: companyData.industry,
        founded: companyData.founded,
        locations: companyData.locations || []
      });
    } else if (tab === 'Posts') {
      await fetchPostsData();
    }
  };

  const fetchPostsData = async () => {
    try {
      const response = await fetch(`/api/company/${companyId}/posts`);
      if (!response.ok) throw new Error('Failed to fetch posts data');
      
      const data = await response.json();
      setTabData(data);
    } catch (err) {
      console.error('Error fetching posts:', err);
      setError(err.message);
    }
  };

  const handleFollow = async () => {
    try {
      const op = isFollowing ? "unfollow" : "follow";
      const response = await fetch(`/api/company/${companyId}/${op}`, {
        method: "POST",
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userId: userId }),
      });
      
      if (!response.ok) throw new Error(`failed in ${op}`);
      
      setIsFollowing(!isFollowing);
      setCompanyData(prev => ({
        ...prev,
        numberOfFollowers: op === "follow" 
          ? prev.numberOfFollowers + 1 
          : prev.numberOfFollowers - 1
      }));
    } catch (err) {
      setError(err.message);
      console.error('Error handleFollow:', err);
    }
  };

  const handleVisitWebsite = () => {
    if (companyData?.website) {
      window.open(companyData.website, '_blank', 'noopener,noreferrer');
    }
  };

  const handleEditClick = (section) => {
    setEditSection(section);
    setShowEditModal(true);
  };

  const handleSaveEdit = async (updatedData) => {
    try {
      const response = await fetch(`/api/company/${companyId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ companyId, ...updatedData}),
      });
      
      if (!response.ok) throw new Error('Failed to update company');
      
      const updated = await response.json();
      setCompanyData(updated);
      
      if (updatedData.locations || activeTab === 'About') {
        setTabData({
          description: updated.description,
          website: updated.website,
          industry: updated.industry,
          founded: updated.founded,
          locations: updated.locations || []
        });
      }
      
      setShowEditModal(false);
      setEditSection(null);

    } catch (err) {
      setError(err.message);
      console.error('Error saving edit:', err);
    }
  };

  if (loading) {
    return (
      <div className="company-profile-container">
        <div className="loading-message">Loading</div>
      </div>
    );
  }

  if (error && !companyData) {
    return (
      <div className="company-profile-container">
        <div className="error-message">Error: {error}</div>
      </div>
    );
  }

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
          isOwner={isOwner}
          onEditCover={() => handleEditClick('cover')}
          onEditLogo={() => handleEditClick('logo')}
        />
        
        <div className="company-info-wrapper">
          <CompanyHeader
            companyName={companyData.companyName}
            description={companyData.description}
            industry={companyData.industry}
            followers={companyData.numberOfFollowers}
            isOwner={isOwner}
            onEditDescription={() => handleEditClick('description')}
          />
          
          <ActionButtons
            onFollow={isOwner ? null : handleFollow}
            onVisitWebsite={handleVisitWebsite}
            isFollowing={isFollowing}
            isOwner={isOwner}
          />
        </div>
      </div>

      <NavigationTabs 
        activeTab={activeTab} 
        onTabChange={handleTabChange} 
        tabs={tabs}
        isOwner={isOwner}
      />

      <div className="company-profile-content">
        {activeTab === 'About' && tabData && (
          <>
            <OverviewSection
              description={tabData.description}
              website={tabData.website}
              industry={tabData.industry}
              founded={tabData.founded}
              isOwner={isOwner}
              onEdit={() => handleEditClick('overview')}
            />
            <LocationsSection 
              locations={tabData.locations || []} 
              isOwner={isOwner}
              onEdit={() => handleEditClick('locations')}
            />
          </>
        )}
        
        {activeTab === 'Posts' && tabData && (
          <PostsTab 
            posts={tabData.posts || []} 
            isOwner={isOwner}
          />
        )}
        
        {!tabData && (
          <div className="loading-message">Loading {activeTab.toLowerCase()} data...</div>
        )}
      </div>

      {showEditModal && (
        <EditModal
          isOpen={showEditModal}
          section={editSection}
          companyData={{companyData}}
          onClose={() => {
            setShowEditModal(false);
            setEditSection(null);
          }}
          onSave={handleSaveEdit}
        />
      )}
    </div>
  );
}