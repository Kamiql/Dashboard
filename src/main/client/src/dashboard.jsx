import React from 'react';
import {Outlet} from 'react-router-dom';

const Dashboard = () => {
  return (
    <div className="dashboard-container">
      <main className="dashboard-main">
        <Outlet />
      </main>
    </div>
  );
};

export default Dashboard;