import { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import {
    isAuthenticated,
  getAuthToken,
  setAuthToken,
  hasUserInfo,
  getUserInfoFromStorage,
  setUserInfo,
  getUserInfo,
  logout,
  login,
  register,
  verifyToken,
  refreshToken,
  getUserId,
  getUserRole,
  hasRole,
} from  "../services/services";
/**
 * ProtectedRoute Component
 * Wraps routes that require authentication
 * Redirects to login if user is not authenticated
 * Automatically fetches user info if token exists but user info is missing
 */
function ProtectedRoute({ children }) {
  const [isLoading, setIsLoading] = useState(false);
  const [isChecking, setIsChecking] = useState(true);
  const [isAuthenticatedState, setIsAuthenticatedState] = useState(false);

  useEffect(() => {
    const fetchUserInfoIfNeeded = async () => {
      const authenticated = isAuthenticated();
      const hasInfo = hasUserInfo();

      setIsAuthenticatedState(authenticated);

      // If user has token but no user info, fetch it
      if (authenticated && !hasInfo) {
        setIsLoading(true);
        const token = getAuthToken();
        try {
          await getUserInfo(token);
        } catch (error) {
          console.error("Failed to fetch user info:", error);
          // If fetching fails (e.g., invalid token), clear auth and redirect
          logout();
          setIsAuthenticatedState(false);
        } finally {
          setIsLoading(false);
          setIsChecking(false);
        }
      } else {
        setIsChecking(false);
      }
    };

    fetchUserInfoIfNeeded();
  }, []);

  // Show loading state while checking/fetching user info
  if (isChecking || isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 via-white to-red-50">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-red-600 mb-4"></div>
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticatedState) {
    // Redirect to login if not authenticated
    return <Navigate to="/login" replace />;
  }

  // Render the protected component if authenticated
  return children;
}

export default ProtectedRoute;
