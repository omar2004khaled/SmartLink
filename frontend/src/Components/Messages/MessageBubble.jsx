
const MessageBubble = ({ message, currentUserId }) => {
  const isSender = message.senderId === currentUserId;

  return (
    <div className={`flex ${isSender ? 'justify-end' : 'justify-start'} mb-4`}>
      <div className={`max-w-[70%] ${isSender ? 'order-2' : 'order-1'}`}>
        <div
          className={`rounded-2xl px-4 py-2 ${
            isSender
              ? 'bg-blue-500 text-white rounded-br-sm'
              : 'bg-gray-200 text-gray-900 rounded-bl-sm'
          }`}
        >
          <p className="text-sm break-words">{message.content}</p>
        </div>

        <div className={`flex items-center gap-1 mt-1 px-2 ${isSender ? 'justify-end' : 'justify-start'}`}>
          <span className="text-xs text-gray-500">
            {new Date(message.createdAt).toLocaleTimeString('en-US', {
              hour: '2-digit',
              minute: '2-digit'
            })}
          </span>
        </div>
      </div>
    </div>
  );
};

export default MessageBubble;
