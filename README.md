# SmartLink — Professional Networking Platform

SmartLink is a full-stack LinkedIn-inspired professional networking platform that connects job seekers and companies. It features AI-powered job recommendations, real-time messaging, CV analysis, social posting, and a full admin portal — all built with a modern React frontend and a Spring Boot backend.

---

## Table of Contents

- [Overview](#overview)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Environment Configuration](#environment-configuration)
- [Running with Docker](#running-with-docker)
- [API Overview](#api-overview)
- [Testing](#testing)
- [Project Structure](#project-structure)

---

## Overview

SmartLink bridges the gap between job seekers and employers through a rich social platform. Users can build detailed profiles, post updates, connect with peers, apply for jobs, and receive AI-ranked job recommendations tailored to their skills and education. Companies get a dedicated portal to post jobs, manage applications, and maintain a company profile. Admins oversee the entire platform through a secure dashboard.

---

## Screenshots

### Landing / Join Page
The entry point where users choose to sign up as a job seeker or a company.

![Join Page](images/join%20page.png)

---

### User Profile Page
Job seekers can showcase their education, experience, skills, and projects. Connections are visible directly from the profile.

![Profile Page](images/profile%20page.png)

---

### Creating a Post
Users compose rich posts with text, media attachments, and reactions — similar to a professional social feed.

![Creating a Post](images/creating%20a%20post.png)

---

### Deleting a Post
Post authors can delete their own content directly from the feed.

![Deleting a Post](images/deleting%20post.png)

---

### Network Page
Discover and manage professional connections. Send, accept, or withdraw connection requests.

![Network Page](images/network%20page.png)

---

### Notifications
Real-time notifications powered by WebSocket keep users updated on connections, reactions, comments, and job activity.

![Notifications](images/notifications.png)

---

### CV Analysis Page
Upload a PDF resume and specify a target position. The AI (powered by Google Gemini) scores the CV and returns strong points, weak points, improvements needed, and an overall score — exportable as a PDF report.

![CV Analysis Page](images/cv%20page.png)

---

### Company Login Page
Dedicated authentication flow for company accounts, separate from job seeker login.

![Company Login Page](images/company%20login%20page.png)

---

### Create Account for Company
Companies register with their details to get access to the employer portal.

![Create Account for Company](images/create%20account%20for%20company.png)

---

### Admin Portal Login
Secure login page exclusively for platform administrators.

![Admin Portal Login](images/admin%20portal%20login%20page.png)

---

### Admin Dashboard
Admins get a high-level overview of platform stats — total users, active users, and admin count — with quick-action navigation.

![Admin Dashboard](images/admin%20dashboard.png)

---

### User Management by Admins
Admins can view, enable/disable, and manage all user accounts on the platform.

![User Management](images/user%20management%20by%20admins.png)

---

### Content Management
Admins can review and remove posts reported by users to keep the platform safe and professional.

![Content Management](images/content%20management.png)

---

## Tech Stack

### Frontend
| Technology | Purpose |
|---|---|
| React 19 + Vite | UI framework and build tool |
| React Router v7 | Client-side routing |
| Tailwind CSS | Utility-first styling |
| MUI (Material UI) | Component library |
| Axios | HTTP client |
| STOMP.js + SockJS | WebSocket real-time messaging |
| jsPDF | CV analysis PDF export |
| Lucide React | Icon library |

### Backend
| Technology | Purpose |
|---|---|
| Spring Boot 3.5 (Java 17) | Core application framework |
| Spring Security + JWT | Authentication & authorization |
| Spring Data JPA + MySQL | Relational data persistence |
| Spring WebSocket (STOMP) | Real-time messaging & notifications |
| Spring GraphQL | Flexible job querying |
| Spring OAuth2 Client | Google social login |
| Spring Mail | Email verification & password reset |
| Redis (Jedis) | Caching AI job recommendations |
| Google Gemini API | AI job recommendations |
| Lombok | Boilerplate reduction |
| JaCoCo | Code coverage (≥65% line coverage enforced) |

### AI Microservice (Python)
| Technology | Purpose |
|---|---|
| FastAPI | REST API for CV analysis |
| Google Gemini API | CV scoring and feedback |
| PyMuPDF / pdfplumber | PDF text extraction |

---

## Features

### For Job Seekers
- **Registration & Authentication** — Email/password signup with email verification, Google OAuth2 login, and password reset via email
- **Rich User Profile** — Education, work experience, skills (with proficiency), projects, and location
- **Social Feed** — Create, react to, and comment on posts with media attachments; report inappropriate content
- **Connections** — Send/accept/withdraw connection requests; browse your network
- **Job Search** — Filter jobs by title, experience level, job type (full-time, part-time, internship, etc.), location type (remote/onsite/hybrid), and salary range via GraphQL
- **AI Job Recommendations** — Gemini-powered ranking of jobs based on your skills and education, cached in Redis
- **CV Analysis** — Upload a PDF CV, get an AI-generated score (0–10), strong/weak points, and improvement suggestions; export the report as PDF
- **Real-time Messaging** — WebSocket-based direct messaging with other users
- **Notifications** — Live notifications for connections, reactions, comments, and job updates

### For Companies
- **Company Registration & Login** — Separate auth flow for employer accounts
- **Company Profile** — Logo, banner, description, overview, locations, and follower count
- **Job Posting** — Create, update, and delete job listings with full details (salary range, deadline, experience level, job type)
- **Application Management** — Review applicants, update application status (pending → reviewed → accepted/rejected)

### For Admins
- **Secure Admin Portal** — Role-based access (`ADMIN` role required)
- **User Management** — View all users, enable/disable accounts
- **Content Moderation** — Review reported posts and remove violating content
- **Platform Stats** — Total users, active users, and admin count at a glance

---

## Architecture

```
SmartLink/
├── frontend/          # React + Vite SPA (port 5173)
├── backendVscode/     # Spring Boot REST + GraphQL + WebSocket API (port 8080)
│   └── src/main/python/resumeAnalyzor/   # FastAPI CV analysis microservice (port 8000)
├── images/            # Project screenshots
└── sql_scripts/       # Database initialization scripts
```

**Request flow:**
1. React frontend communicates with the Spring Boot backend over REST and GraphQL
2. Real-time features (chat, notifications) use STOMP over WebSocket
3. CV analysis requests go directly to the FastAPI Python microservice
4. The Spring Boot backend calls Google Gemini for AI job recommendations and caches results in Redis
5. Media uploads (profile pictures, post attachments) go to Cloudinary

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 18+ and npm
- MySQL 8+
- Redis (or use the provided Docker Compose)
- Python 3.10+ (for CV analysis microservice)

### 1. Database Setup

Run the SQL scripts in order:

```sql
-- 1. Main schema
source sql_scripts/SmartLink.sql

-- 2. Company data
source sql_scripts/company_script.sql

-- 3. Posts integration
source sql_scripts/posts_integration.sql

-- 4. Messaging schema
source sql_scripts/message_Script.sql
```

### 2. Backend Setup

```bash
cd backendVscode

# Copy and configure properties
# Edit src/main/resources/application.properties with your credentials

mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 3. Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

The app will be available at `http://localhost:5173`.

### 4. CV Analysis Microservice Setup

```bash
cd backendVscode/src/main/python/resumeAnalyzor

pip install fastapi uvicorn python-dotenv pymupdf

# Create a .env file
echo "GEMINI_API_KEY=your_gemini_api_key" > .env

uvicorn main:app --reload --port 8000
```

---

## Environment Configuration

### Backend — `application.properties`

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/smartlink
spring.datasource.username=<your_db_user>
spring.datasource.password=<your_db_password>

# JWT
jwt.secret=<your_long_secret_key>
jwt.expiration-ms=86400000

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=<your_google_client_id>
spring.security.oauth2.client.registration.google.client-secret=<your_google_client_secret>

# Email (Gmail SMTP)
spring.mail.username=<your_gmail>
spring.mail.password=<your_app_password>

# Gemini AI
Gemini_key=<your_gemini_api_key>

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# CORS
app.cors.allowed-origins=http://localhost:5173
app.frontend.base-url=http://localhost:5173
app.backend.base-url=http://localhost:8080
```

### Frontend — `.env` (optional, defaults shown)

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_CV_ANALYSIS_API_URL=http://localhost:8000
VITE_FRONTEND_BASE_URL=http://localhost:5173
VITE_CLOUDINARY_UPLOAD_URL=https://api.cloudinary.com/v1_1/<your_cloud>/auto/upload
```

---

## Running with Docker

A `docker-compose.yml` is provided to spin up Redis alongside the Spring Boot app:

```bash
cd backendVscode
docker-compose up --build
```

This starts:
- `smartlink-redis` on port `6379`
- `smartlink-app` (Spring Boot) on port `8080`

> Make sure MySQL is running and accessible before starting the containers.

---

## API Overview

### REST Endpoints (Spring Boot — port 8080)

| Domain | Base Path |
|---|---|
| Authentication | `/auth/**` |
| User Profile | `/api/profiles/**` |
| Posts | `/Post/**` |
| Comments & Likes | `/comment/**` |
| Reactions | `/reactions/**` |
| Connections | `/api/connections/**` |
| Jobs (REST) | `/jobs/**` |
| Job Applications | `/apply/**` |
| Company Profile | `/api/company/**` |
| Messaging | `/api/messages/**` |
| Notifications | `/api/notifications/**` |
| Search | `/api/search/**` |
| AI Recommendations | `/gemini/**` |
| Admin | `/admin/**` *(ADMIN role required)* |
| WebSocket | `/ws/**` |

### GraphQL Endpoint

`POST /graphql`

Supports flexible job querying with filters:

```graphql
query {
  allJobs(filter: {
    experienceLevel: MID
    jobType: FULL_TIME
    locationType: REMOTE
    minSalary: 50000
  }) {
    jobId
    title
    companyName
    salaryMin
    salaryMax
    deadline
    isApplied
  }
}
```

### CV Analysis Microservice (FastAPI — port 8000)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/analyze-cv` | Upload PDF + position, returns AI analysis |
| `GET` | `/health` | Health check |

---

## Testing

The backend has comprehensive test coverage enforced by JaCoCo (minimum 65% line coverage).

```bash
cd backendVscode

# Run all tests
mvn test

# Generate coverage report (output: target/site/jacoco/index.html)
mvn test jacoco:report
```

Test categories include:
- Unit tests for all services (Auth, Post, Job, Connection, Notification, Gemini, etc.)
- Controller integration tests with MockMvc
- DTO and entity validation tests
- Security configuration tests (JWT, OAuth2)

---

## Project Structure

```
backendVscode/src/main/java/com/example/auth/
├── config/          # Security, JWT, CORS, WebSocket, OAuth2, Redis config
├── controller/      # REST controllers (Auth, Post, Job, Profile, Admin, etc.)
├── dto/             # Request/Response data transfer objects
├── entity/          # JPA entities (User, Post, Job, Connection, Message, etc.)
├── enums/           # ApplicationStatus, JobType, ReactionType, etc.
├── exceptions/      # Global exception handlers
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic layer
└── utility/         # Constants and Redis key helpers

frontend/src/
├── Components/      # Feature components (Profile, Jobs, Admin, Messages, etc.)
├── PostCard/        # Post display with reactions, comments, reports
├── PostComposotion/ # Post creation UI
├── JobPage/         # Company job management UI
├── contexts/        # React context (Alert notifications)
├── hooks/           # Custom hooks (useAlert, useWebSocket)
└── config.js        # Centralized API URL configuration
```

---

## CI/CD

GitHub Actions workflow (`.github/workflows/maven.yml`) automatically builds and tests the backend on every push.

---

## License

This project was developed as part of a Software Design course. All rights reserved by the SmartLink team.
