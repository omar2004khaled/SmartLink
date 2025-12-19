import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Search, Home, Briefcase, Users, User, LogOut, Menu, X } from 'lucide-react';
import './Navbar.css';
import { API_BASE_URL } from '../config';
import Logo from '../assets/Logo.png';

const Navbar = ({ showSearch = false }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleSearch = async (query) => {
    if (query.length < 2) {
      setSearchResults([]);
      setShowResults(false);
      return;
    }

    setIsSearching(true);
    try {
      const currentUserId = localStorage.getItem('userId');
      const response = await fetch(`${API_BASE_URL}/api/search/users?query=${encodeURIComponent(query)}&currentUserId=${currentUserId}`);
      const data = await response.json();

      if (response.ok) {
        setSearchResults(data.results || []);
        setShowResults(true);
      }
    } catch (error) {
      console.error('Search error:', error);
    } finally {
      setIsSearching(false);
    }
  };

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchQuery(value);
    handleSearch(value);
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login-select');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <>
      {/* Top Header */}
      <header className="top-header">
        <div className="header-content">
          <button className="menu-toggle" onClick={() => setSidebarOpen(!sidebarOpen)}>
            <Menu size={24} />
          </button>
          <div className="brand-section">
            <img src={Logo} alt="SmartLink" className="brand-logo" />
            <h1 className="brand-title">SmartLink</h1>
          </div>
          {showSearch && (
            <div className="header-search">
              <Search className="search-icon" size={18} />
              <input
                type="text"
                placeholder="Search users..."
                value={searchQuery}
                onChange={handleSearchChange}
                className="search-input"
              />
              {showResults && (
                <div className="search-results">
                  {isSearching ? (
                    <div className="search-loading">Searching...</div>
                  ) : searchResults.length > 0 ? (
                    searchResults.map(user => (
                      <div key={user.id} className="search-result-item" onClick={() => navigate(user.userType === 'COMPANY' ? `/company-profile/${user.id}` : `/profile/${user.id}`)}>
                        <div className="result-avatar">{user.fullName.charAt(0)}</div>
                        <div className="result-info">
                          <div className="result-name">{user.fullName}</div>
                          <div className="result-email">{user.email}</div>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="search-no-results">No users found</div>
                  )}
                </div>
              )}
            </div>
          )}
        </div>
      </header>

      {/* Sidebar Navigation */}
      <nav className={`sidebar ${sidebarOpen ? 'sidebar-open' : ''}`}>
        <div className="sidebar-header">
          <button className="sidebar-close" onClick={() => setSidebarOpen(false)}>
            <X size={20} />
          </button>
        </div>
        <div className="sidebar-content">
          <div className="nav-section">
            <h3 className="nav-section-title">Main</h3>
            <button className={`sidebar-link ${isActive('/home') ? 'active' : ''}`} onClick={() => { navigate('/home'); setSidebarOpen(false); }}>
              <Home size={20} />
              <span>Home</span>
            </button>
            <button className={`sidebar-link ${(isActive('/profile') && !location.search.includes('tab=connections')) ? 'active' : ''}`} onClick={() => { navigate('/profile'); window.location.reload(); setSidebarOpen(false); }}>
              <User size={20} />
              <span>Profile</span>
            </button>
          </div>

          <div className="nav-section">
            <h3 className="nav-section-title">Network</h3>
            <button className={`sidebar-link ${location.search.includes('tab=connections') ? 'active' : ''}`} onClick={() => { navigate('/profile?tab=connections'); setSidebarOpen(false); }}>
              <Users size={20} />
              <span>Connections</span>
            </button>
            <button className={`sidebar-link ${isActive('/profile') ? 'active' : ''}`} onClick={() => { navigate('/job'); setSidebarOpen(false); }}>
              <Briefcase size={20} />
              <span>Opportunities</span>
            </button>
          </div>

          <div className="nav-section">
            <button className="sidebar-link logout" onClick={handleLogout}>
              <LogOut size={20} />
              <span>Sign Out</span>
            </button>
          </div>
        </div>
      </nav>

      {/* Sidebar Overlay */}
      {sidebarOpen && <div className="sidebar-overlay" onClick={() => setSidebarOpen(false)} />}
    </>
  );
};

export default Navbar;