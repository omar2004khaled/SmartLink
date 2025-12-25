import Photo from './Photo';

const ConversationItem = ({ conversation, currentUserId, isActive, onClick, unreadCount }) => {
  const isUnread = conversation.receiverId === currentUserId && !conversation.isRead;
  const isCompany = conversation.otherUserType === 'COMPANY';

  return (
    <div
      onClick={onClick}
      className={`p-4 border-b cursor-pointer transition-colors ${
        isActive ? 'bg-blue-50 border-l-4 border-l-blue-500' : 'hover:bg-gray-50'
      }`}
    >
      <div className="flex items-start gap-3">
        <Photo isCompany={isCompany} picture={conversation.otherUserProfilePicture} />

        <div className="flex-1 min-w-0">
          <div className="flex items-center justify-between mb-1">
            <h3 className={`font-semibold truncate ${isUnread ? 'text-gray-900' : 'text-gray-700'}`}>
              {conversation.otherUserName || `User ${conversation.otherUserId}`}
            </h3>
            <span className="text-xs text-gray-500">
              {new Date(conversation.createdAt).toLocaleTimeString('en-US', {
                hour: '2-digit',
                minute: '2-digit'
              })}
            </span>
          </div>

          <div className="flex items-center justify-between">
            <p className={`text-sm truncate ${isUnread ? 'font-semibold text-gray-900' : 'text-gray-600'}`}>
              {conversation.content}
            </p>
            {unreadCount > 0 && (
              <span className="ml-2 w-3 h-3 bg-blue-500 rounded-full inline-block" aria-hidden="true" />
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConversationItem;