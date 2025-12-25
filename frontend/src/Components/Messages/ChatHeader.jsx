import { X } from 'lucide-react';
import Photo from './Photo';

const ChatHeader = ({ otherUser, onClose }) => {
  const isCompany = otherUser?.userType === 'COMPANY';

  return (
    <div className="h-16 bg-white border-b flex items-center justify-between px-6 shadow-sm">
      <div className="flex items-center gap-3">
        <Photo isCompany={isCompany} picture={otherUser?.profilePicture}/>
        <div>
          <h2 className="font-semibold text-gray-900">
            {otherUser?.name}
          </h2>
        </div>
      </div>
      
      <button
        onClick={onClose}
        className="p-2 hover:bg-gray-100 rounded-full transition-colors lg:hidden"
      >
        <X size={20} />
      </button>
    </div>
  );
};

export default ChatHeader;