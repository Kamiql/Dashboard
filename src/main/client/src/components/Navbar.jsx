import React from 'react';
import '../css/components/navbar.css';
import {useNavigate} from 'react-router-dom';

function Navbar() {
    const navigate = useNavigate()

    return (
    <nav className="navbar">
      <a href='/app/dashboard'>
        <h1 className="navbar-brand">Dashboard - {localStorage.getItem("theme") || "default"} </h1>
      </a>

      <div className="nav-icons">
        <div className="nav-item" onClick={() => navigate('/app/dashboard/settings')}>
          <i className="fas fa-gears"/>
          <div className="info">Settings</div>
        </div>
        <div className="nav-item">
          <i className="fas fa-circle-user"/>
          <div className="info">User</div>
        </div>
        <div className="nav-item" onClick={() => navigate('/logout')}>
          <i className="fas fa-arrow-right-from-bracket"/>
          <div className="info">Exit</div>
        </div>
      </div>
    </nav>
    );
}

export default Navbar;
