import React from 'react';

function GuildList({ botData, onGuildClick }) {
    return (
        <>
            {botData && botData.guilds.map(guild => (
                <a
                    className="guild"
                    key={guild.guildId}
                    onClick={() => onGuildClick(guild)}
                >
                    <button>
                        <p>{guild.guildName}</p>
                    </button>
                </a>
            ))}
        </>
    );
}

export default GuildList;
