import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Home, Briefcase, User, LogOut, Menu, X } from 'lucide-react';
import './Navbar.css';
import NotificationBell from './NotificationBell';

const CompanyNavbar = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
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
            <img src="/src/assets/Logo.png" alt="SmartLink" className="brand-logo" />
            <h1 className="brand-title">SmartLink</h1>
          </div>
          <NotificationBell />
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
            <button className={`sidebar-link ${isActive('/company-home') ? 'active' : ''}`} onClick={() => { navigate('/company-home'); setSidebarOpen(false); }}>
              <Home size={20} />
              <span>Home</span>
            </button>
            <button className={`sidebar-link ${isActive('/company-jobs') ? 'active' : ''}`} onClick={() => { navigate('/company-jobs'); setSidebarOpen(false); }}>
              <Briefcase size={20} />
              <span>Jobs</span>
            </button>
            <button className={`sidebar-link ${isActive('/company-profile') ? 'active' : ''}`} onClick={() => { navigate('/company-profile'); setSidebarOpen(false); }}>
              <User size={20} />
              <span>Profile</span>
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

export default CompanyNavbar;
