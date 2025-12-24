import React, { createContext, useState, useCallback } from 'react';

export const AlertContext = createContext();

export const AlertProvider = ({ children }) => {
    const [alerts, setAlerts] = useState([]);

    const showAlert = useCallback((message, type = 'info') => {
        const id = Date.now() + Math.random();
        const newAlert = { id, message, type };

        setAlerts(prev => [...prev, newAlert]);

        // Auto-dismiss after 4 seconds
        setTimeout(() => {
            setAlerts(prev => prev.filter(alert => alert.id !== id));
        }, 4000);

        return id;
    }, []);

    const removeAlert = useCallback((id) => {
        setAlerts(prev => prev.filter(alert => alert.id !== id));
    }, []);

    const showSuccess = useCallback((message) => showAlert(message, 'success'), [showAlert]);
    const showError = useCallback((message) => showAlert(message, 'error'), [showAlert]);
    const showInfo = useCallback((message) => showAlert(message, 'info'), [showAlert]);
    const showWarning = useCallback((message) => showAlert(message, 'warning'), [showAlert]);

    const value = {
        alerts,
        showAlert,
        showSuccess,
        showError,
        showInfo,
        showWarning,
        removeAlert
    };

    return (
        <AlertContext.Provider value={value}>
            {children}
        </AlertContext.Provider>
    );
};
