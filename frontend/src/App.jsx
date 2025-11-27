import './App.css';
import UserProfile from './Components/Profile/UserProfile/UserProfile.jsx';

export default function App() {
  const searchParams = new URLSearchParams(window.location.search);
  const profileId = searchParams.get('profileId') || '1';
  return <UserProfile profileId={profileId} />;
}
