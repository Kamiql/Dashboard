import React, { useEffect, useState } from 'react';
import Navbar from "../../components/Navbar";
import Footer from "../../components/Footer";
import GuildList from "./bot/guilds.jsx";
import '../../css/pages/modules/bot.css';

function Bot() {
    const [botData, setBotData] = useState(null);
    const [selectedGuild, setSelectedGuild] = useState(null);

    useEffect(() => {
        fetch('/api/v1/bot/self', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                setBotData(data);
                console.log(data);
            })
            .catch(error => console.error('Error fetching bot data:', error));
    }, []);

    const bannerStyle = botData?.bannerUrl
        ? { backgroundImage: `url(${botData.bannerUrl})` }
        : { backgroundColor: 'var(--accent-color)' };

    const handleGuildClick = (guild) => {
        setSelectedGuild(guild);
        window.history.pushState(null, '', `?guild=${guild.guildId}`);
    };

    const closeOverlay = () => {
        setSelectedGuild(null);
        window.history.pushState(null, '', window.location.pathname);
    };

    return (
        <div className="app-container">
            <Navbar />
            <main className="main-content">
                <div className="parent">
                    <div className="div1" style={bannerStyle}>
                        {botData ? (
                            <>
                                <p><img src={botData.avatarUrl} alt="Avatar"/></p>
                                <a>
                                    <p><strong>Username:</strong> {botData.username}</p>
                                    <p><strong>ID:</strong> {botData.id}</p>
                                </a>
                            </>
                        ) : (
                            <p>Loading bot data...</p>
                        )}
                    </div>
                    <div className="div2">
                        <GuildList botData={botData} onGuildClick={handleGuildClick}/>
                    </div>
                </div>

                    {selectedGuild && (
                        <div className="overlay" onClick={closeOverlay}>
                            <div className="overlay-content" onClick={(e) => e.stopPropagation()}>
                                <button className="close-btn" onClick={closeOverlay}>×</button>
                                <h2>Guild Details</h2>
                                <p><strong>Name:</strong> {selectedGuild.guildName}</p>
                                <p><strong>ID:</strong> {selectedGuild.guildId}</p>
                            </div>
                        </div>
                    )}
            </main>
            <Footer />
        </div>
    );
}

export default Bot;
