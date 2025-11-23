import React from 'react';

export default function NavigationTabs({ activeTab, onTabChange, tabs}) {
  return (
    <div>
      {tabs.map((tab) => (
        <button
          key={tab}
          onClick={() => onTabChange(tab)}
          style={{fontWeight: activeTab=== tab? 'bold' : 'normal'}}
        >
          {tab}
        </button>
      ))}
    </div>
  );
}