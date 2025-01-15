import '@fortawesome/fontawesome-free/css/all.min.css';
import React, { useEffect, useState } from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';

import NotFound from "./pages/NotFound.jsx";
import Bot from "./pages/modules/Bot.jsx";
import Settings from "./pages/modules/Settings.jsx";
import Home from "./pages/Dashboard.jsx";
import Dashboard from "./dashboard.jsx";
import Server from "./pages/modules/Server.jsx";

const roleHierarchy = ['DEFAULT', 'STAFF', 'ADMIN', 'DEVELOPER'];

const hasAccess = (userRole, requiredRole) => {
  const userIndex = roleHierarchy.indexOf(userRole);
  const requiredIndex = roleHierarchy.indexOf(requiredRole);
  return userIndex >= requiredIndex;
};

const ProtectedRoute = ({ element, requiredRole, userRole }) => {
  return hasAccess(userRole, requiredRole) ? element : <Navigate to="/app/dashboard" replace />;
};

const AppRouter = ({ userRole }) => {
  const router = createBrowserRouter([
    {
      path: '/app/dashboard',
      element: <Dashboard />,
      children: [
        { path: 'bot', element: <ProtectedRoute element={<Bot />} requiredRole="ADMIN" userRole={userRole} /> },
        { path: 'settings', element: <ProtectedRoute element={<Settings />} requiredRole="DEFAULT" userRole={userRole} /> },
        { path: 'server', element: <ProtectedRoute element={<Server />} requiredRole="DEVELOPER" userRole={userRole} /> },
        { index: true, element: <Home /> },
      ],
      errorElement: <NotFound />,
    },
    {
      path: '/',
      element: <Navigate to="/app/dashboard" replace />,
      errorElement: <NotFound />,
    },
  ]);

  return <RouterProvider router={router} />;
};

const App = () => {
  const [userRole, setUserRole] = useState('DEFAULT');

  useEffect(() => {
    fetch('/api/v1/user/session', {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    })
      .then((response) => response.json())
      .then((data) => {
        setUserRole(data.parent ?? 'DEFAULT');
      })
      .catch(() => setUserRole('DEFAULT'));
  }, []);

  return <AppRouter userRole={userRole} />;
};

const theme = localStorage.getItem('theme') ? localStorage.getItem('theme') : 'dark';
document.documentElement.setAttribute('data-theme', theme);

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
