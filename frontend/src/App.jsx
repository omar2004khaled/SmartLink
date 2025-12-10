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
import Posts from './Components/Posts';
import UserProfile from './Components/Profile/UserProfile/UserProfile';
import UserTypeSelection from './Components/UserTypeSelection';
import LoginTypeSelection from './Components/LoginTypeSelection';
import CompanySignUp from './Components/CompanySignUp';
import CompanyLogin from './Components/CompanyLogin';
import CompanyProfilePage from './Components/CompanyProfilePage';
import MainPage from './Components/MainPage';
import JobsPage from './Components/job/JobsPage';
import CompanyJobsPage from './JobPage/CompanyJobsPage';

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
    </Routes>
  );
}

export default App;
