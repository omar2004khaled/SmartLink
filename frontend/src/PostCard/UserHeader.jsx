import React, { useState, useRef, useEffect } from "react";
import avatar from './avatar.png';
import { Link } from 'react-router-dom';
import { Delete, MoreVertical } from 'lucide-react';
import { userIdFromLocalStorage } from "../FetchData/FetchData";

function UserHeader({ username = 'User', userId, time = "18 hours ago", avatarUrl = null, bio = '', userType = null, onReport = null, onSnooze = null, onDelete = null, onUpdate = null, postId = null }) {
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
        if (typeof onReport === 'function') {
            onReport();
        } else {
            alert('Post reported. Thank you for your feedback.');
        }
    }

    function handleSnooze() {
        setMenuOpen(false);
        if (typeof onSnooze === 'function') {
            onSnooze();
        } else {
            alert(`Snoozed ${username} for 30 days.`);
        }
    }
    function handleDelete() {
        setMenuOpen(false);
        if (typeof onDelete === 'function') {
            onDelete();
        } else {
            alert('Delete function not available');
        }
    }

    function handleUpdate() {
        setMenuOpen(false);
        if (typeof onUpdate === 'function') {
            onUpdate();
        } else {
            alert('Update function not available');
        }
    }

    function handleMenuKeyDown(e) {
        if (e.key === 'Escape') setMenuOpen(false);
    }

    return (
        <div className="user-header">
            <Link to={userType === 'COMPANY' ? `/company-profile/${userId}` : `/profile/${userId}`} className="user-link">
                <img
                    src={avatarUrl || avatar}
                    alt={`${username} avatar`}
                    className="user-avatar"
                    onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = placeholder; }}
                />
            </Link>

            <div className="user-info">
                <Link to={userType === 'COMPANY' ? `/company-profile/${userId}` : `/profile/${userId}`} className="user-link">
                    <span className="user-username">{username}</span>
                </Link>
                {bio && <span className="user-bio">{bio}</span>}
            </div>

            <span className="post-timestamp">{time}</span>

            <div className="post-menu-container" ref={menuRef} onKeyDown={handleMenuKeyDown}>
                <button
                    className="three-dots-btn"
                    aria-haspopup="true"
                    aria-expanded={menuOpen}
                    onClick={(e) => { e.stopPropagation(); setMenuOpen((s) => !s); }}
                    onKeyDown={(e) => {
                        if (e.key === 'Enter' || e.key === ' ') {
                            e.preventDefault();
                            setMenuOpen((s) => !s);
                        }
                    }}
                    title="Post options"
                    aria-label="Post options"
                >
                    <MoreVertical size={18} />
                </button>

                {menuOpen && (
                    userIdFromLocalStorage() === userId ? (<>
                        <ul className="post-menu" role="menu">
                            <li>
                                <button className="post-menu-item" role="menuitem" onClick={handleUpdate}>Edit post</button>
                            </li>
                            <li>
                                <button className="post-menu-item" role="menuitem" onClick={handleDelete}>Delete post</button>
                            </li>
                        </ul>
                    </>) : (<>
                        <ul className="post-menu" role="menu">
                            <li>
                                <button className="post-menu-item" role="menuitem" onClick={handleReport}>Report post</button>
                            </li>
                            <li>
                                <button className="post-menu-item" role="menuitem" onClick={handleSnooze}>Snooze {username} for 30 days</button>
                            </li>
                        </ul>
                    </>)
                )}
            </div>
        </div>
    );
}

export default UserHeader;