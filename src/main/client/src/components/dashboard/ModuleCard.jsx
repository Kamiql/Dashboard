import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';

function ModuleCard({ name, role, icon, enabled, redirect }) {
  const [userRole, setUserRole] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetch('/api/v1/user/session', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((response) => response.json())
      .then((data) => {
        setUserRole(data.parent);
      });
  }, []);

  const canAccessModule = () => {
    if (!userRole) return false;
    const roleHierarchy = ['DEFAULT', 'STAFF', 'ADMIN', 'DEVELOPER'];
    const requiredRoleIndex = roleHierarchy.indexOf(role);
    const userRoles = userRole.split(',').map(r => r.trim());
    const userRoleIndices = userRoles.map(r => roleHierarchy.indexOf(r));

    return userRoleIndices.some(index => index >= requiredRoleIndex) && enabled;
  };

  return (
    <div
      className={`module-card ${enabled ? 'enabled' : 'disabled'}`}
      onClick={() => canAccessModule() && navigate(redirect)}
    >
      <div className="module-icon">
        <i className={icon}></i>
      </div>
      <div className="module-content">
        <h3>{name}</h3>
        <div className="module-info">
          <p>
            <i className={canAccessModule() ? 'fas fa-lock-open' : 'fas fa-lock'}></i> {role}
          </p>
          <p>
            <i className={enabled ? 'fas fa-toggle-on' : 'fas fa-toggle-off'}></i>{' '}
            {enabled ? 'Enabled' : 'Disabled'}
          </p>
        </div>
      </div>
    </div>
  );
}

export default ModuleCard;
