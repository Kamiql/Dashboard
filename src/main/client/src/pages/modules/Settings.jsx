import React, {useState} from 'react';
import '../../css/pages/modules/settings.css';
import Navbar from '../../components/Navbar';

import Appearance from '../../components/dashboard/settings/Appearance';
import Notifications from '../../components/dashboard/settings/Notifications';
import Account from '../../components/dashboard/settings/Account';

function Settings() {
  const [activeTab, setActiveTab] = useState('appearance');

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  return (
    <div>
      <Navbar />
      <div className="settings-container">
        <div className="sidebar">
          <h2>Settings</h2>
          <ul>
            <li onClick={() => handleTabClick('appearance')}>
              <a href="#appearance" className={activeTab === 'appearance' ? 'active' : ''}>Appearance</a>
            </li>
            <li onClick={() => handleTabClick('notifications')}>
              <a href="#notifications" className={activeTab === 'notifications' ? 'active' : ''}>Notifications</a>
            </li>
            <li onClick={() => handleTabClick('account')}>
              <a href="#account" className={activeTab === 'account' ? 'active' : ''}>Account</a>
            </li>
          </ul>
        </div>
        <div className="content">
          {activeTab === 'appearance' && <Appearance />}
          {activeTab === 'notifications' && <Notifications />}
          {activeTab === 'account' && <Account />}
        </div>
      </div>
    </div>
  );
}

export default Settings;
