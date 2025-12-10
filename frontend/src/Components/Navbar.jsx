import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Search, Home, Briefcase, Users, User, LogOut } from 'lucide-react';
import './Navbar.css';

const Navbar = ({ showSearch = false }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [showResults, setShowResults] = useState(false);

  const handleSearch = async (query) => {
    if (query.length < 2) {
      setSearchResults([]);
      setShowResults(false);
      return;
    }

    setIsSearching(true);
    try {
      const currentUserId = localStorage.getItem('userId');
      const response = await fetch(`http://localhost:8080/api/search/users?query=${encodeURIComponent(query)}&currentUserId=${currentUserId}`);
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
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand" onClick={() => navigate('/home')}>SmartLink</div>
        
        {/* Search Bar - Only show on home page */}
        {showSearch && (
          <div className="search-container">
            <Search className="search-icon" size={20} />
            <input
              type="text"
              placeholder="Search users..."
              value={searchQuery}
              onChange={handleSearchChange}
              className="search-input"
            />
            
            {/* Search Results Dropdown */}
            {showResults && (
              <div className="search-results">
                {isSearching ? (
                  <div className="search-loading">Searching...</div>
                ) : searchResults.length > 0 ? (
                  searchResults.map(user => (
                    <div key={user.id} className="search-result-item" onClick={() => navigate(`/profile/${user.id}`)}>
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

        {/* Navigation Links */}
        <div className="nav-links">
          <button className={`nav-link ${isActive('/home') ? 'active' : ''}`} onClick={() => navigate('/home')}>
            <Home size={20} />
            <span>Home</span>
          </button>
          <button className="nav-link">
            <Briefcase size={20} />
            <span>Jobs</span>
          </button>
          <button className="nav-link">
            <Users size={20} />
            <span>Connections</span>
          </button>
          <button className={`nav-link ${isActive('/profile') ? 'active' : ''}`} onClick={() => navigate('/profile')}>
            <User size={20} />
            <span>Profile</span>
          </button>
          <button className="nav-link logout" onClick={handleLogout}>
            <LogOut size={20} />
            <span>Logout</span>
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;