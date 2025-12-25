import React, { useState, useEffect, useRef, useCallback, useMemo } from 'react';
import { Bell, X, Check, CheckCheck, UserPlus, UserCheck, Heart, MessageCircle, ThumbsUp, Briefcase, FileText, Edit3, Building2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { API_BASE_URL } from '../config';
import useWebSocket from '../hooks/useWebSocket';
import './NotificationBell.css';

const NotificationBell = () => {
    const navigate = useNavigate();
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [isOpen, setIsOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const dropdownRef = useRef(null);

    const handleWebSocketNotification = useCallback((notification) => {
        ////console.log('Real-time notification received:', notification);

        setNotifications(prev => [notification, ...prev]);


        if (notification.isRead !== true) {
            setUnreadCount(prev => {
                const newCount = prev + 1;
                ////console.log('Unread count updated from', prev, 'to', newCount);
                return newCount;
            });
        }
    }, []);

    const userId = useMemo(() => localStorage.getItem('userId'), []);
    useWebSocket(userId, handleWebSocketNotification);

    useEffect(() => {
        fetchUnreadCount();
        const interval = setInterval(fetchUnreadCount, 30000);
        return () => clearInterval(interval);
    }, []);

    useEffect(() => {
        if (isOpen) {
            fetchNotifications();
        }
    }, [isOpen]);


    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        };

        if (isOpen) {
            document.addEventListener('mousedown', handleClickOutside);
        }
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isOpen]);

    const fetchUnreadCount = async () => {
        try {
            const userId = localStorage.getItem('userId');
            if (!userId) return;

            const response = await fetch(`${API_BASE_URL}/api/notifications/unread/count?userId=${userId}`);
            if (response.ok) {
                const count = await response.json();
                setUnreadCount(count);
            }
        } catch (error) {
            console.error('Error fetching unread count:', error);
        }
    };

    const fetchNotifications = async () => {
        setLoading(true);
        try {
            const userId = localStorage.getItem('userId');
            if (!userId) return;

            const response = await fetch(`${API_BASE_URL}/api/notifications/all?userId=${userId}`);
            if (response.ok) {
                const data = await response.json();
                setNotifications(data);
            }
        } catch (error) {
            console.error('Error fetching notifications:', error);
        } finally {
            setLoading(false);
        }
    };

    const markAsRead = async (notificationId) => {
        try {
            const userId = localStorage.getItem('userId');
            const response = await fetch(
                `${API_BASE_URL}/api/notifications/${notificationId}/read?userId=${userId}`,
                { method: 'PUT' }
            );

            if (response.ok) {
                setNotifications(prev =>
                    prev.map(notif =>
                        notif.notificationId === notificationId ? { ...notif, isRead: true } : notif
                    )
                );
                fetchUnreadCount();
            }
        } catch (error) {
            console.error('Error marking notification as read:', error);
        }
    };

    const markAllAsRead = async () => {
        try {
            const userId = localStorage.getItem('userId');
            const response = await fetch(
                `${API_BASE_URL}/api/notifications/read-all?userId=${userId}`,
                { method: 'PUT' }
            );

            if (response.ok) {
                setNotifications(prev => prev.map(notif => ({ ...notif, isRead: true })));
                setUnreadCount(0);
            }
        } catch (error) {
            console.error('Error marking all as read:', error);
        }
    };

    const deleteNotification = async (notificationId) => {
        try {
            const userId = localStorage.getItem('userId');
            const response = await fetch(
                `${API_BASE_URL}/api/notifications/${notificationId}?userId=${userId}`,
                { method: 'DELETE' }
            );

            if (response.ok) {
                setNotifications(prev => prev.filter(notif => notif.notificationId !== notificationId));
                fetchUnreadCount();
            }
        } catch (error) {
            console.error('Error deleting notification:', error);
        }
    };

    const handleNotificationClick = (notification) => {
        markAsRead(notification.notificationId);

        // Navigate based on notification type
        switch (notification.type) {
            case 'CONNECTION_REQUEST':
            case 'CONNECTION_ACCEPTED':
                navigate('/profile?tab=connections');
                break;
            case 'POST_LIKE':
            case 'POST_COMMENT':
            case 'NEW_POST':
                if (notification.relatedEntityId) {
                    navigate(`/home?postId=${notification.relatedEntityId}`);
                } else {
                    navigate('/home');
                }
                break;
            case 'JOB_APPLICATION':
                // Navigate to applications page filtered by job (relatedEntityId is the jobId)
                if (notification.relatedEntityId) {
                    navigate(`/applications?jobId=${notification.relatedEntityId}`);
                } else {
                    navigate('/applications');
                }
                break;
            case 'JOB_APPLICATION_STATUS_CHANGE':
            case 'APPLICATION_COMMENT':
                // Navigate to applications page for status updates and comments
                navigate('/applications');
                break;
            case 'NEW_JOB_FROM_FOLLOWED_COMPANY':
                if (notification.relatedEntityId) {
                    navigate(`/job?jobId=${notification.relatedEntityId}`);
                } else {
                    navigate('/job');
                }
                break;
            case 'COMPANY_FOLLOWED':
                // Navigate to the follower's profile (relatedEntityId is the follower's user ID)
                if (notification.relatedEntityId) {
                    navigate(`/profile/${notification.relatedEntityId}`);
                } else {
                    navigate('/profile');
                }
                break;
            default:
                break;
        }
        setIsOpen(false);
    };

    const getNotificationIcon = (type) => {
        const iconMap = {
            CONNECTION_REQUEST: <UserPlus size={20} />,
            CONNECTION_ACCEPTED: <UserCheck size={20} />,
            POST_LIKE: <Heart size={20} />,
            POST_COMMENT: <MessageCircle size={20} />,
            COMMENT_LIKE: <ThumbsUp size={20} />,
            JOB_APPLICATION: <Briefcase size={20} />,
            JOB_APPLICATION_STATUS_CHANGE: <FileText size={20} />,
            NEW_POST: <Edit3 size={20} />,
            COMPANY_FOLLOWED: <Building2 size={20} />,
            APPLICATION_COMMENT: <MessageCircle size={20} />,
            NEW_JOB_FROM_FOLLOWED_COMPANY: <Briefcase size={20} />
        };
        return iconMap[type] || <Bell size={20} />;
    };

    const formatTime = (dateString) => {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now - date;
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMs / 3600000);
        const diffDays = Math.floor(diffMs / 86400000);

        if (diffMins < 1) return 'Just now';
        if (diffMins < 60) return `${diffMins}m ago`;
        if (diffHours < 24) return `${diffHours}h ago`;
        if (diffDays < 7) return `${diffDays}d ago`;
        return date.toLocaleDateString();
    };

    return (
        <div className="notification-bell-container" ref={dropdownRef}>
            <button
                className="notification-bell-button"
                onClick={() => setIsOpen(!isOpen)}
                aria-label="Notifications"
            >
                <Bell size={20} />
                {unreadCount > 0 && (
                    <span className="notification-badge">
                        {unreadCount > 99 ? '99+' : unreadCount}
                    </span>
                )}
            </button>

            {isOpen && (
                <div className="notification-dropdown">
                    <div className="notification-header">
                        <h3>Notifications</h3>
                        {notifications.some(n => !n.isRead) && (
                            <button
                                className="mark-all-read-btn"
                                onClick={markAllAsRead}
                                title="Mark all as read"
                            >
                                <CheckCheck size={18} />
                            </button>
                        )}
                    </div>

                    <div className="notification-list">
                        {loading ? (
                            <div className="notification-loading">Loading...</div>
                        ) : notifications.length === 0 ? (
                            <div className="notification-empty">
                                <Bell size={48} className="empty-icon" />
                                <p>No notifications yet</p>
                            </div>
                        ) : (
                            notifications.map((notification) => (
                                <div
                                    key={notification.notificationId}
                                    className={`notification-item ${!notification.isRead ? 'unread' : ''}`}
                                >
                                    <div
                                        className="notification-content"
                                        onClick={() => handleNotificationClick(notification)}
                                    >
                                        <div className="notification-item-icon">
                                            {getNotificationIcon(notification.type)}
                                        </div>
                                        <div className="notification-text">
                                            <div className="notification-title">{notification.title}</div>
                                            <div className="notification-message">{notification.message}</div>
                                            <div className="notification-time">{formatTime(notification.createdAt)}</div>
                                        </div>
                                    </div>
                                    <div className="notification-actions">
                                        {!notification.isRead && (
                                            <button
                                                className="mark-read-btn"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    markAsRead(notification.notificationId);
                                                }}
                                                title="Mark as read"
                                            >
                                                <Check size={16} />
                                            </button>
                                        )}
                                        <button
                                            className="delete-btn"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                deleteNotification(notification.notificationId);
                                            }}
                                            title="Delete"
                                        >
                                            <X size={16} />
                                        </button>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default NotificationBell;
