import React from 'react';
import './NavigationTabs.css';

export default function NavigationTabs({ activeTab, onTabChange, tabs}) {
  return (
    <div className="navigation-tabs">
      {tabs.map((tab) => (
        <button
          key={tab}
          onClick={() => onTabChange(tab)}
          className={`tab-button ${activeTab=== tab? 'active' : ''}`}
        >
          {tab}
        </button>
      ))}
    </div>
  );
}