import { Routes, Route } from 'react-router-dom';
import SignUp from "./components/SignUp";
import Login from "./components/Login";
import VerifyEmail from "./components/VerifyEmail";
import EmailVerified from "./components/EmailVerified";
import ForgotPassword from './components/ForgotPassword';
import ResetPassword from './components/ResetPassword';
import OAuthCallback from "./components/OAuthCallback";
import Dashboard from "./components/Dashboard";
import PostComposotion from './PostComposotion/PostComposotion';
import PostCard from './PostCard/PostCard';
import UserProfile from './Components/Profile/UserProfile/UserProfile';
import JobsPage from './Components/job/JobsPage';
import CompanyJobsPage from './JobPage/CompanyJobsPage';
import Posts from './Components/Posts';
import UserTypeSelection from './Components/UserTypeSelection';
import LoginTypeSelection from './Components/LoginTypeSelection';
import CompanySignUp from './Components/CompanySignUp';
import CompanyLogin from './Components/CompanyLogin';
import CompanyProfilePage from './Components/CompanyProfilePage';
import CompanyHome from './Components/CompanyHome';
import CompanyJobs from './Components/CompanyJobs';
import MainPage from './Components/MainPage';
import AdminLogin from './Components/AdminLogin';
import AdminDashboard from './Components/AdminDashboard';

function App() {
  return (
    <Routes>
      {/* User Type Selection */}
      <Route path="/" element={<UserTypeSelection />} />
      <Route path="/home" element={<MainPage />} />
      <Route path="/signup-select" element={<UserTypeSelection />} />
      <Route path="/login-select" element={<LoginTypeSelection />} />
      
      {/* Job Seeker Routes */}
      <Route path="/signup" element={<SignUp />} />
      <Route path="/login" element={<Login />} />
      <Route path="/profile" element={<UserProfile />} />
      <Route path="/profile/:userId" element={<UserProfile />} />
      
      {/* Company Routes */}
      <Route path="/company/signup" element={<CompanySignUp />} />
      <Route path="/company/login" element={<CompanyLogin />} />
      <Route path="/company-home" element={<CompanyHome />} />
      <Route path="/company-jobs" element={<CompanyJobs />} />
      <Route path="/company-profile" element={<CompanyProfilePage />} />
      
      {/* Common Routes */}
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route path="/verify-email" element={<VerifyEmail />} />
      <Route path="/email-verified" element={<EmailVerified />} />
      <Route path="/auth/callback" element={<OAuthCallback />} />
      <Route path="/PostComposotion" element={<PostComposotion />} />
      <Route path="/posts" element={<Posts />} />
      <Route path="/post" element={<PostCard />} />
      <Route path="/job" element={<JobsPage/>} />
      <Route path="/jobs" element={<CompanyJobsPage/>} />
      
      {/* Admin Routes */}
      <Route path="/admin/login" element={<AdminLogin />} />
      <Route path="/admin/dashboard" element={<AdminDashboard />} />
    </Routes>
  );
}

export default App;
