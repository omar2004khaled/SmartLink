import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Building2, User, Shield } from 'lucide-react';

const UserTypeSelection = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen w-full grid grid-cols-1 md:grid-cols-2">
      {/* Left Panel */}
      <div className="hidden md:flex flex-col justify-between p-12 bg-[#FFEAEE] dark:bg-[#2C1A1D] text-center">
        <div className="flex justify-between items-start w-full">
          <div className="flex items-center gap-2">
            <img 
              src="src/assets/Logo.png" 
              alt="Logo"
              className="h-12 w-auto object-contain"
            />
            <span className="text-2xl font-bold text-gray-800 dark:text-gray-200">Smart Link</span>
          </div>
          <Link 
            to="/admin/login" 
            className="flex items-center gap-2 px-3 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Shield size={16} />
            Admin
          </Link>
        </div>
        <div className="flex flex-col gap-6">
          <h1 className="text-gray-800 dark:text-gray-200 text-5xl font-black leading-tight tracking-[-0.033em]">
            Connect. Collaborate. Grow.
          </h1>
          <h2 className="text-gray-700 dark:text-gray-300 text-base font-normal leading-normal">
            Choose your account type to get started
          </h2>
        </div>
        <div></div>
      </div>

      {/* Right Panel - Selection */}
      <div className="flex items-center justify-center p-6 sm:p-8 lg:p-12 bg-background">
        <div className="w-full max-w-md flex flex-col gap-8">
          <div className="flex flex-col gap-4 text-center">
            <h1 className="text-3xl font-bold text-foreground">Join Smart Link</h1>
            <p className="text-muted-foreground">
              Select how you want to use Smart Link
            </p>
          </div>

          <div className="flex flex-col gap-4">
            {/* Job Seeker Option */}
            <button
              onClick={() => navigate('/signup')}
              className="group relative flex flex-col items-center gap-4 p-8 rounded-xl border-2 border-border hover:border-primary hover:bg-primary/5 transition-all duration-200"
            >
              <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center group-hover:bg-primary/20 transition-colors">
                <User className="w-8 h-8 text-primary" />
              </div>
              <div className="flex flex-col gap-2">
                <h3 className="text-xl font-semibold text-foreground">Job Seeker</h3>
                <p className="text-sm text-muted-foreground">
                  Find opportunities, build your network, and advance your career
                </p>
              </div>
            </button>

            {/* Company Option */}
            <button
              onClick={() => navigate('/company/signup')}
              className="group relative flex flex-col items-center gap-4 p-8 rounded-xl border-2 border-border hover:border-primary hover:bg-primary/5 transition-all duration-200"
            >
              <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center group-hover:bg-primary/20 transition-colors">
                <Building2 className="w-8 h-8 text-primary" />
              </div>
              <div className="flex flex-col gap-2">
                <h3 className="text-xl font-semibold text-foreground">Company</h3>
                <p className="text-sm text-muted-foreground">
                  Post jobs, find talent, and grow your company's presence
                </p>
              </div>
            </button>
          </div>

          {/* Login Link */}
          <div className="text-center border-t border-border pt-6">
            <p className="text-sm text-muted-foreground">
              Already have an account?{' '}
              <button 
                onClick={() => navigate('/login-select')}
                className="font-semibold text-primary hover:underline"
              >
                Log In
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserTypeSelection;
