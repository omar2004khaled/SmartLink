// Centralized frontend configuration
// Uses Vite env vars when available, with sensible local defaults.

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const CV_ANALYSIS_API_URL =
  import.meta.env.VITE_CV_ANALYSIS_API_URL || "http://localhost:8000";

const FRONTEND_BASE_URL =
  import.meta.env.VITE_FRONTEND_BASE_URL || "http://localhost:5173";

// OAuth endpoints
export const GOOGLE_OAUTH2_AUTH_URL = `${API_BASE_URL}/oauth2/authorization/google`;

// API base
export { API_BASE_URL, CV_ANALYSIS_API_URL, FRONTEND_BASE_URL };

// Third-party integrations
export const CLOUDINARY_UPLOAD_URL =
  import.meta.env.VITE_CLOUDINARY_UPLOAD_URL ||
  "https://api.cloudinary.com/v1_1/dqhdiihx4/auto/upload";


