import { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import ConversationItem from './ConversationItem';
import MessageBubble from './MessageBubble';
import ChatHeader from './ChatHeader';
import MessageInput from './MessageInput';

const API_BASE_URL = 'http://localhost:8080/api';
const WS_URL = 'http://localhost:8080/ws';

const api = {
  sendMessage: async (message) => {
    const response = await fetch(`${API_BASE_URL}/messages`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(message)
    });
    return response.json();
  },

  getConversation: async (user1, user2, page = 0) => {
    const response = await fetch(
      `${API_BASE_URL}/messages/conversation?user1=${user1}&user2=${user2}&page=${page}&size=20`
    );
    return response.json();
  },

  getConversations: async (userId, page = 0) => {
    const response = await fetch(
      `${API_BASE_URL}/messages/inbox?userId=${userId}&page=${page}&size=20`
    );
    return response.json();
  },

  markAsRead: async (senderId, receiverId) => {
    await fetch(
      `${API_BASE_URL}/messages/read?senderId=${senderId}&receiverId=${receiverId}`,
      { method: 'POST' }
    );
  }
};

const MessagingApp = ({ userId,openChatWith }) => {
  const [currentUserId] = useState(() => userId);
  const [conversations, setConversations] = useState([]);
  const [selectedChat, setSelectedChat] = useState(null);
  const [selectedChatUser, setSelectedChatUser] = useState(null);
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const stompClient = useRef(null);
  const messagesEndRef = useRef(null);
  const hasOpenedChat = useRef(false);

  const scrollToBottom = (behavior = 'smooth') => {
    messagesEndRef.current?.scrollIntoView({ behavior });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    const socket = new SockJS(WS_URL);
    const client = Stomp.over(socket);

    client.connect({}, () => {
      client.subscribe(`/topic/messages/${currentUserId}`, (message) => {
        const receivedMessage = JSON.parse(message.body);

        if (selectedChat && (receivedMessage.senderId === selectedChat || receivedMessage.receiverId === selectedChat)) {
          setMessages((prev) => [...prev, receivedMessage]);
          if (receivedMessage.receiverId === currentUserId) {
            api.markAsRead(receivedMessage.senderId, currentUserId);
          }
        }
        loadConversations();
      });
    });

    stompClient.current = client;

    return () => {
      if (client?.connected) client.disconnect();
    };
  }, [currentUserId, selectedChat]);

  const loadConversations = async () => {
    try {
      const data = await api.getConversations(currentUserId);
      setConversations(data.content || []);
      setError(null);
    } catch {
      setError('Failed to load conversations.');
    }
  };

  const loadMessages = async (otherUserId) => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.getConversation(currentUserId, otherUserId);
      setMessages((data.content || []).reverse());

      await api.markAsRead(otherUserId, currentUserId);
      loadConversations();
      setTimeout(() => scrollToBottom('auto'), 0);
    } catch {
      setError('Failed to load messages.');
    } finally {
      setLoading(false);
    }
  };

  const sendMessage = async (content) => {
    if (!selectedChat || !content.trim()) return;

    const messageRequest = {
      senderId: currentUserId,
      receiverId: selectedChat,
      content
    };

    try {
      if (stompClient.current?.connected) {
        stompClient.current.send(
          '/app/chat.sendMessage',
          {},
          JSON.stringify(messageRequest)
        );
      } else {
        const response = await api.sendMessage(messageRequest);
        setMessages((prev) => [...prev, response]);
      }
    } catch {
      setError('Failed to send message');
    }
    if(openChatWith) loadConversations();
  };

  const handleChatSelect = (conversation) => {
    setSelectedChat(conversation.otherUserId);
    setSelectedChatUser({
      id: conversation.otherUserId,
      name: conversation.otherUserName,
      profilePicture: conversation.otherUserProfilePicture,
      userType: conversation.otherUserType
    });
    loadMessages(conversation.otherUserId);
  };

  useEffect(() => {
    loadConversations();
  }, []);
  useEffect(() => {
    if (openChatWith && !hasOpenedChat.current) {
      hasOpenedChat.current = true;
 
      setSelectedChat(openChatWith.otherUserId);
      setSelectedChatUser({
        id: openChatWith.otherUserId,
        name: openChatWith.otherUserName,
        profilePicture: openChatWith.otherUserProfilePicture,
        userType: openChatWith.otherUserType
      });
      loadMessages(openChatWith.otherUserId);
    }
  }, [openChatWith]);

  return (
    <div className="h-screen bg-gray-50 flex flex-col">
      <div className="h-16 bg-white border-b flex items-center justify-between px-6 shadow-sm">
        <h1 className="text-xl font-bold">Messages</h1>
      </div>

     {error && (
        <div className="bg-red-50 border-b border-red-200 px-6 py-3">
          <p className="text-sm text-red-800">{error}</p>
        </div>
      )}

      <div className="flex-1 flex overflow-hidden">
        <div className={`${selectedChat ? 'hidden lg:block' : ''} w-full lg:w-96 bg-white border-r`}>
          {conversations.length === 0 ? (
            <div className="p-4 text-center text-gray-500">
              No conversations yet
            </div>
          ) : (
            conversations.map((conv) => (
              <ConversationItem
                key={conv.id}
                conversation={conv}
                currentUserId={currentUserId}
                isActive={selectedChat === conv.otherUserId}
                onClick={() => handleChatSelect(conv)}
                unreadCount={conv.receiverId === currentUserId && !conv.isRead ? 1 : 0}
              />
            ))
          )}
        </div>

        <div className={`${!selectedChat ? 'hidden lg:flex' : 'flex'} flex-1 flex-col`}>
          {selectedChat ? (
            <>
              <ChatHeader
                otherUser={selectedChatUser}
                onClose={() => {
                  setSelectedChat(null);
                  setSelectedChatUser(null);
                  hasOpenedChat.current = false;
                }}
              />

              <div className="flex-1 overflow-y-auto p-4">
                {messages.length === 0 && (
                  <div className="flex items-center justify-center h-full text-gray-500">
                    No messages yet
                  </div>
                )}
                {messages.map((msg) => (
                  <MessageBubble
                    key={msg.id}
                    message={msg}
                    currentUserId={currentUserId}
                  />
                ))}
                <div ref={messagesEndRef} />
              </div>

              <MessageInput onSend={sendMessage} disabled={loading} />
            </>
          ) : (
            <div className="flex items-center justify-center h-full text-gray-500">
              Select a conversation
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default MessagingApp;