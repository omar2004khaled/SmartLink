# SmartLink Authentication + Posts Integration

## Overview
This integration combines the authentication system with the posts feed functionality, creating a seamless user experience where users are redirected to the posts feed after successful login.

## Key Changes Made

### Frontend Changes
1. **App.jsx**: Restructured routing to include authentication flow with protected routes
2. **PostsFeed.jsx**: New component containing the original posts functionality with authentication-aware header
3. **Login.jsx**: Updated to redirect to `/posts` instead of `/dashboard` after successful login
4. **Dashboard.jsx**: Updated to include navigation to posts feed
5. **OAuthCallback.jsx**: Updated to redirect to posts feed after OAuth login
6. **Port Updates**: All API calls updated from port 8080 to 8081

### Backend Changes
1. **application.properties**: Updated server port to 8081 and OAuth redirect URI accordingly

### Database
1. **posts_integration.sql**: SQL script to ensure proper database structure for posts

## Application Flow

### Authentication Flow
1. User visits `/` → Redirected to `/posts` (protected route)
2. If not authenticated → Redirected to `/login`
3. After successful login → Redirected to `/posts`
4. OAuth login also redirects to `/posts`

### Posts Feed Features
- View all posts with pagination
- Create new posts with media upload
- Authentication-aware header with logout functionality
- Protected route - requires valid auth token

## Routes Structure
```
/ → Redirects to /posts
/login → Login page
/signup → Registration page
/posts → Posts feed (protected)
/dashboard → Success page with navigation options (protected)
/oauth/callback → OAuth callback handler
/forgot-password → Password reset request
/reset-password → Password reset form
/verify-email → Email verification
/email-verified → Email verification success
```

## Running the Application

### Backend (Port 8081)
```bash
cd backendVscode
mvn spring-boot:run
```

### Frontend (Port 5173 - Vite default)
```bash
cd frontend
npm install
npm run dev
```

### Database Setup
1. Ensure MySQL is running on localhost:3306
2. Create database `smartlink`
3. Run the integration SQL script if needed:
```sql
source sql_scripts/posts_integration.sql
```

## API Endpoints Used
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `GET /Post/all` - Fetch posts with pagination
- `POST /Post/add` - Create new post
- OAuth endpoints for Google authentication

## Environment Configuration
- Backend: `http://localhost:8081`
- Frontend: `http://localhost:5173`
- Database: `mysql://localhost:3306/smartlink`

## Security Features
- Protected routes using localStorage token validation
- CORS configuration for cross-origin requests
- JWT token-based authentication
- OAuth2 integration with Google

## Next Steps
1. Test the complete authentication → posts flow
2. Verify all API endpoints work with port 8081
3. Test post creation and media upload functionality
4. Ensure OAuth flow works correctly
5. Test logout functionality and route protection