import { User, Building2 } from 'lucide-react';

const Photo = ({ isCompany,picture }) => {
    return (
         <div className="relative">
          {picture ? (
            <img
              src={picture}
              className="w-12 h-12 rounded-full object-cover"
            />
          ) : (
            <div className={`w-12 h-12 rounded-full ${
              isCompany 
                ? 'bg-gradient-to-br from-purple-400 to-pink-500' 
                : 'bg-gradient-to-br from-blue-400 to-purple-500'
            } flex items-center justify-center text-white font-semibold`}>
              {isCompany ? <Building2 size={24} /> : <User size={24} />}
            </div>
          )}
        </div>

    );
   

}
export default Photo;