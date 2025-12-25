import { useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { API_BASE_URL } from '../config';

/**
 * Custom hook for WebSocket connection to receive real-time notifications
 * @param {string} userId - The user ID to subscribe to notifications for
 * @param {function} onNotificationReceived - Callback function when a notification is received
 * @returns {object} - WebSocket connection status and methods
 */
const useWebSocket = (userId, onNotificationReceived) => {
    const clientRef = useRef(null);
    const isConnectedRef = useRef(false);

    const connect = useCallback(() => {
        if (!userId || isConnectedRef.current) return;

        try {
            const socket = new SockJS(`${API_BASE_URL}/ws`);

            // Create STOMP client
            const stompClient = new Client({
                webSocketFactory: () => socket,
                debug: (str) => {
                    //console.log('STOMP Debug:', str);
                },
                reconnectDelay: 5000,
                heartbeatIncoming: 10000,
                heartbeatOutgoing: 10000,
            });

            stompClient.onConnect = () => {
                //console.log('WebSocket Connected');
                isConnectedRef.current = true;


                stompClient.subscribe(`/topic/notifications/${userId}`, (message) => {
                    try {
                        const notification = JSON.parse(message.body);
                        //console.log('Received notification:', notification);
                        onNotificationReceived(notification);
                    } catch (error) {
                        console.error('Error parsing notification:', error);
                    }
                });
            };

            stompClient.onStompError = (frame) => {
                console.error('WebSocket STOMP error:', frame);
                console.error('Error headers:', frame.headers);
                console.error('Error body:', frame.body);
                isConnectedRef.current = false;
            };

            stompClient.onDisconnect = () => {
                //console.log(' WebSocket Disconnected');
                isConnectedRef.current = false;
            };

            stompClient.onWebSocketError = (event) => {
                console.error('WebSocket transport error:', event);
            };

            //console.log('Attempting to connect to:', `${API_BASE_URL}/ws`);
            stompClient.activate();
            clientRef.current = stompClient;

        } catch (error) {
            console.error('Failed to connect WebSocket:', error);
            console.error('Error details:', error.message, error.stack);
            isConnectedRef.current = false;
        }
    }, [userId, onNotificationReceived]);

    const disconnect = useCallback(() => {
        if (clientRef.current) {
            clientRef.current.deactivate();
            clientRef.current = null;
            isConnectedRef.current = false;
            //console.log('WebSocket disconnected manually');
        }
    }, []);

    useEffect(() => {
        connect();
        return () => disconnect();
    }, [connect, disconnect]);

    return {
        isConnected: isConnectedRef.current,
        disconnect,
        reconnect: connect
    };
};

export default useWebSocket;
