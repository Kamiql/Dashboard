import React from 'react';
import '../css/pages/notfound.css';

function NotFound() {
  return (
    <div className="not-found">
      <h1 className="not-found-title">404</h1>
      <p className="not-found-message">Die Seite, die Sie suchen, wurde nicht gefunden.</p>
      <a href="/app/dashboard" className="not-found-link">Zurück zur Startseite</a>
    </div>
  );
}

export default NotFound;
