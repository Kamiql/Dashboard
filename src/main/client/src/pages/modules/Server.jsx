import React from 'react';
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";
import '../../css/pages/modules/bot.css';

function Server() {
    return (
        <div className="app-container">
            <Navbar/>
            <main className="main-content">
                <div className="parent">
                    <div className="div1">1</div>
                    <div className="div2">2</div>
                    <div className="div3">3</div>
                    <div className="div4">4</div>
                    <div className="div5">5</div>
                    <div className="div6">6</div>
                </div>
            </main>
            <Footer/>
        </div>
    );
}

export default Server;