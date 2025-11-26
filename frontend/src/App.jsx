import { Routes, Route } from 'react-router-dom';
import SignUp from "./components/SignUp";
import Login from "./components/Login";
import VerifyEmail from "./components/VerifyEmail";
import EmailVerified from "./components/EmailVerified";
import ForgotPassword from './components/ForgotPassword';
import ResetPassword from './components/ResetPassword';

function App() {
  return (
    <Routes>
      <Route path="/signup" element={<SignUp />} />
      <Route path="/login" element={<Login />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/reset-password" element={<ResetPassword />} />
      <Route path="/verify-email" element={<VerifyEmail />} />
      <Route path="/email-verified" element={<EmailVerified />} />
      <Route path="/" element={<Login />} />
    </Routes>
  );
}

export default App;