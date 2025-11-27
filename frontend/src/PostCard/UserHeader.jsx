import React, { useState, useRef, useEffect } from "react";
import avatar from './avatar.png';
import { Link } from 'react-router-dom';

function UserHeader({ username = 'User' , time="18 hours ago" }) {
    const placeholder = `data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='120' height='120'><rect fill='%23eef2ff' width='100%' height='100%'/><text x='50%' y='50%' font-size='40' text-anchor='middle' fill='%230f172a' dy='.3em'>U</text></svg>`;
    const [menuOpen, setMenuOpen] = useState(false);
    const menuRef = useRef(null);

    useEffect(() => {
        function onDocClick(e) {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setMenuOpen(false);
            }
        }
        document.addEventListener('click', onDocClick);
        return () => document.removeEventListener('click', onDocClick);
    }, []);

    function handleReport() {
        setMenuOpen(false);
        alert('Post reported. Thank you for your feedback.');
    }

    function handleSnooze() {
        setMenuOpen(false);
        alert(`Snoozed ${username} for 30 days.`);
    }

    return (
        <div className="user-header">
            <Link to={`/profile/${username}`} className="user-link">
                <img
                    src={avatar}
                    alt="user-avatar"
                    className="user-avatar"
                    onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = placeholder; }}
                />
            </Link>

            <div className="user-info">
                <Link to={`/profile/${username}`} className="user-link">
                    <span className="user-username">{username}</span>
                </Link>
                <span className="user-bio">User biooooooooooooooooooooo</span>
            </div>

            <span className="post-timestamp">{time}</span>

            <div className="post-menu-container" ref={menuRef}>
                <button
                    className="three-dots-btn"
                    aria-haspopup="true"
                    aria-expanded={menuOpen}
                    onClick={(e) => { e.stopPropagation(); setMenuOpen((s) => !s); }}
                    title="Post options"
                >
                    &#x22EF;
                </button>

                {menuOpen && (
                    <ul className="post-menu" role="menu">
                        <li className="post-menu-item" role="menuitem" onClick={handleReport}>Report post</li>
                        <li className="post-menu-item" role="menuitem" onClick={handleSnooze}>Snooze {username} for 30 days</li>
                    </ul>
                )}
            </div>
        </div>
    );
}

export default UserHeader;