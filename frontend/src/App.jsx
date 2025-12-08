import { Routes, Route } from 'react-router-dom';
import SignUp from "./components/SignUp";
import Login from "./components/Login";
import VerifyEmail from "./components/VerifyEmail";
import EmailVerified from "./components/EmailVerified";
import ForgotPassword from './components/ForgotPassword';
import ResetPassword from './components/ResetPassword';
import OAuthCallback from "./components/OAuthCallback";
import PostComposotion from './components/PostComposotion/PostComposotion';
import PostCard from './components/PostCard/PostCard';
import ProtectedRoute from './pages/protected';


function App() {
  return (
    <Routes>
      <Route path="/signup" element={<ProtectedRoute><SignUp /></ProtectedRoute>} />
      <Route path="/login" element={<Login />} />
      <Route path="/forgot-password" element={<ProtectedRoute><ForgotPassword /></ProtectedRoute>} />
      <Route path="/reset-password" element={<ProtectedRoute><ResetPassword /></ProtectedRoute>} />
      <Route path="/verify-email" element={<ProtectedRoute><VerifyEmail /></ProtectedRoute>} />
      <Route path="/email-verified" element={<ProtectedRoute><EmailVerified /></ProtectedRoute>} />
      <Route path="/auth/callback" element={<ProtectedRoute><OAuthCallback /></ProtectedRoute>} />
      <Route path="/PostComposation" element={<ProtectedRoute><PostComposotion /></ProtectedRoute>} />
      <Route path="/post" element={<ProtectedRoute><PostCard /></ProtectedRoute>} />
      {/* <Route path="/" element={<ProtectedRoute><Profile /></ProtectedRoute>} /> */}
    </Routes>
  );
}

export default App;
