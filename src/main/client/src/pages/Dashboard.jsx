import React from 'react';
import ModuleList from '../components/dashboard/ModuleList.jsx';
import Navbar from '../components/Navbar.jsx';
import Footer from '../components/Footer.jsx';
import '../css/pages/dashboard.css';

function Home() {
  return (
    <div className="app-container">
      <Navbar />
      <main className="main-content">
        <section className="home">
          <h1>Module Übersicht</h1>
          <ModuleList />
        </section>
      </main>
      <Footer />
    </div>
  );
}

export default Home;
