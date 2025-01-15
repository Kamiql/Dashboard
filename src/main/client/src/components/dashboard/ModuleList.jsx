import React from 'react';
import ModuleCard from './ModuleCard.jsx';

const modules = [
  { id: 1, name: 'Database', role: 'DEVELOPER', icon: 'fas fa-database', enabled: false, redirect: '/app/dashboard/dev/database'},
  { id: 2, name: 'Statistics', role: 'DEFAULT', icon: 'fas fa-chart-line', enabled: false, redirect: '/app/dashboard/statics'},
  { id: 3, name: 'Server', role: 'DEVELOPER', icon: 'fas fa-server', enabled: true, redirect: '/app/dashboard/server'},
  { id: 4, name: 'Bot', role: 'ADMIN', icon: 'fa-brands fa-discord', enabled: true, redirect: '/app/dashboard/bot'},
];

function ModuleList() {
  return (
    <div className="module-list">
      {modules.map((module) => (
        <ModuleCard key={module.id} {...module}/>
      ))}
    </div>
  );
}

export default ModuleList;
