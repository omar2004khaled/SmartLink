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
import { API_BASE_URL } from '../../../../config';

export default function CompanyProfile({ companyId, userId, targetUserId, currentUserId }) {
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

  // Handle backward compatibility or simplified usage
  const viewerId = currentUserId || userId;
  const profileOwnerId = targetUserId;

  useEffect(() => {
    if (companyId || profileOwnerId || viewerId) {
      fetchCompanyBasicData();
    }
  }, [companyId, profileOwnerId, viewerId]);

  const fetchCompanyBasicData = async () => {
    try {
      setLoading(true);
      setError(null);

      let url;
      if (companyId) {
        url = viewerId
          ? `${API_BASE_URL}/api/company/${companyId}?userId=${viewerId}`
          : `${API_BASE_URL}/api/company/${companyId}`;
      } else if (profileOwnerId) {
        // Fetch by Target User ID
        url = `${API_BASE_URL}/api/company/user/${profileOwnerId}`;
      } else {
        // Fallback: Fetch by Viewer ID (My Profile)
        url = `${API_BASE_URL}/api/company/user/${viewerId}`;
      }

      const token = localStorage.getItem('authToken');
      const headers = {
        'Content-Type': 'application/json',
      };
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const response = await fetch(url, {
        method: 'GET',
        headers,
      });


      if (!response.ok) {
        const errorText = await response.text();
        console.error('Error response:', errorText);
        throw new Error(`Failed to fetch company data: ${response.status}`);
      }

      const contentType = response.headers.get('content-type');
      if (!contentType || !contentType.includes('application/json')) {
        throw new Error('Server returned non-JSON response. Check if API endpoint exists.');
      }

      const data = await response.json();
      console.log('Company data received:', data);
      setCompanyData(data);
      setIsFollowing(data.isFollowing || false);
      setIsOwner(Number(data.userId) === Number(viewerId));

      // Set companyId if not provided (for API calls)
      if (!companyId && data.companyProfileId) {
        companyId = data.companyProfileId;
      }

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
      // If we don't have companyId from props, use the one from data
      const idToUse = companyId || companyData?.companyProfileId;
      if (!idToUse) throw new Error("No company ID available");

      const response = await fetch(`${API_BASE_URL}/api/company/${idToUse}/posts`, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) throw new Error('Failed to fetch posts data');

      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        const data = await response.json();
        setTabData(data);
      } else {
        setTabData({ posts: [] });
      }
    } catch (err) {
      console.error('Error fetching posts:', err);
      setError(err.message);
      setTabData({ posts: [] });
    }
  };

  const handleFollow = async () => {
    try {
      const op = isFollowing ? "unfollow" : "follow";
      const token = localStorage.getItem('authToken');
      const headers = {
        'Content-Type': 'application/json',
      };
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const idToUse = companyId || companyData?.companyProfileId;
      const response = await fetch(`${API_BASE_URL}/api/company/${idToUse}/${op}`, {
        method: "POST",
        headers,
        body: JSON.stringify({ userId: viewerId }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Follow error:', errorText);
        throw new Error(`Failed to ${op}`);
      }

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
      const url = companyData.website.startsWith('http')
        ? companyData.website
        : `https://${companyData.website}`;
      window.open(url, '_blank', 'noopener,noreferrer');
    }
  };

  const handleEditClick = (section) => {
    setEditSection(section);
    setShowEditModal(true);
  };

  const handleSaveEdit = async (updatedData) => {
    try {
      const token = localStorage.getItem('authToken');
      const headers = {
        'Content-Type': 'application/json',
      };
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }

      const currentCompanyId = companyId || companyData?.companyProfileId;
      const response = await fetch(`${API_BASE_URL}/api/company/${currentCompanyId}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify({
          companyId: currentCompanyId,
          ...updatedData
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Update error:', errorText);
        throw new Error('Failed to update company');
      }

      const contentType = response.headers.get('content-type');
      if (!contentType || !contentType.includes('application/json')) {
        throw new Error('Server returned non-JSON response');
      }

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
        <div className="error-message">
          <h3>Error Loading Company</h3>
          <p>{error}</p>
          <button onClick={fetchCompanyBasicData}>Retry</button>
        </div>
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
          companyData={companyData}
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
