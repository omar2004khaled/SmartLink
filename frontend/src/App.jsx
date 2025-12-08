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
import Posts from './components/Posts';
import UserProfile from './Components/Profile/UserProfile/UserProfile';

function App() {
  return (
    <Routes>
      <Route path="/signup" element={<SignUp />} />
      <Route path="/login" element={<Login />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route path="/verify-email" element={<VerifyEmail />} />
      <Route path="/email-verified" element={<EmailVerified />} />
      <Route path="/auth/callback" element={<OAuthCallback />} />
      <Route path="/PostComposotion" element={<PostComposotion />} />
      <Route path="/posts" element={<Posts />} />
      <Route path="/profile" element={<UserProfile />} />
      <Route path="/" element={<Login />} />
      <Route path="/post" element={<PostCard />} />
    </Routes>
  );
}

export default App;
