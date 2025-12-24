import React from 'react';
import { useAlert } from '../../hooks/useAlert';
import './AlertNotification.css';

const AlertNotification = () => {
    const { alerts, removeAlert } = useAlert();

    const getIcon = (type) => {
        switch (type) {
            case 'success':
                return (
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <path d="M5 13L9 17L19 7" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
                    </svg>
                );
            case 'error':
                return (
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                        <circle cx="10" cy="10" r="9" stroke="currentColor" strokeWidth="2" />
                        <path d="M7 7L13 13M13 7L7 13" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
                    </svg>
                );
            case 'warning':
                return (
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                        <path d="M10 2L18 17H2L10 2Z" stroke="currentColor" strokeWidth="2" strokeLinejoin="round" />
                        <path d="M10 8V11" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
                        <circle cx="10" cy="14" r="0.5" fill="currentColor" />
                    </svg>
                );
            case 'info':
            default:
                return (
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                        <circle cx="10" cy="10" r="9" stroke="currentColor" strokeWidth="2" />
                        <path d="M10 10V14" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
                        <circle cx="10" cy="7" r="0.5" fill="currentColor" />
                    </svg>
                );
        }
    };

    return (
        <div className="alert-container">
            {alerts.map((alert) => (
                <div key={alert.id} className={`alert alert-${alert.type}`}>
                    <div className="alert-icon">{getIcon(alert.type)}</div>
                    <div className="alert-message">{alert.message}</div>
                    <button
                        className="alert-close"
                        onClick={() => removeAlert(alert.id)}
                        aria-label="Close alert"
                    >
                        ×
                    </button>
                </div>
            ))}
        </div>
    );
};

export default AlertNotification;
