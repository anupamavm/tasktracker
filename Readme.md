# TaskTracker

TaskTracker is a full-stack task management application with a Spring Boot backend and a Next.js frontend. It supports user authentication, task CRUD operations, filtering, and role-based admin actions.

## Setup Instructions

### Backend setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Start the PostgreSQL database and pgAdmin from the root folder:
   ```bash
   docker compose up -d
   ```
3. Make sure Java 21 and Maven are installed.
4. Set the required environment variables in `.env` before running the app:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/tasktrackerdb
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=postgres
   ```
5. Run the backend:
   ```bash
   ./mvnw spring-boot:run
   ```
6. The API will be available at http://localhost:8080.

### Frontend setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Open http://localhost:3000 in your browser.

### Environment configuration

- The backend reads its datasource settings from environment variables in the Spring configuration.
- A temporary JWT secret is defined in the backend configuration for local development.
- Docker Compose provisions PostgreSQL and pgAdmin for local development.

### Database setup

- PostgreSQL runs on port 5433 locally.
- pgAdmin is available at http://localhost:5050.
- Hibernate is configured to update the schema automatically with `ddl-auto: update` (Only recommend for development environment).

## Design Decisions

### Architecture overview

- The frontend is built with Next.js and uses a client-side auth context for session handling.
- The backend is implemented with Spring Boot in layered architecure with clear seperation of concerns and exposes REST APIs for authentication, tasks, and admin functions.
- PostgreSQL stores application data and can be viewd using pgAdmin, while Docker provides a repeatable local environment.

### Key implementation decisions

- JWT-based authentication is used for stateless access control.
- Role-based authorization is enforced for admin-only actions.
- New users are created with the default `USER` role; the first admin can be created by registering a user and then promoting that account through the admin role-change flow.
- Controllers, services, DTOs, and repositories are separated to keep the backend structured and maintainable.
- Task operations are exposed through a REST API that supports pagination and filtering.

## Assumptions

- Local development is expected to use Docker for PostgreSQL instead of a managed database.
- The application is intended for a single local environment and does not yet include production-grade secrets management.
- The current scope focuses on core task tracking and role-based admin control rather than multi-tenant or enterprise features.
- Admin role assignment is handled through a simple role-change workflow rather than introducing a separate onboarding or approval role; this was chosen to simplify implementation and can be improved in the future with a more structured admin-management flow.

## Future Improvements

- Add a stronger automated test suite for backend services and frontend flows.
- Improve environment handling with a dedicated `.env` approach and production-safe secrets.
- Add a more robust admin onboarding flow, such as seeded admin accounts or a dedicated approval process, instead of relying on direct role changes.
- Add a full one-command Docker setup for the frontend and backend together.
