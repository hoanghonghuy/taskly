# Taskly - Task Management Application

Taskly is a full-stack task management application with a modern web interface. It provides comprehensive features for managing tasks, projects, and tags with secure authentication using JWT.

## Tech Stack

### Backend
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.9
- **Database:** PostgreSQL 16
- **Build Tool:** Maven
- **Security:** Spring Security, OAuth2 Resource Server (JWT)
- **Tools:** Lombok

### Frontend
- **Framework:** React 19.2.0
- **Language:** TypeScript 5.9.3
- **Build Tool:** Vite 7.2.4
- **Routing:** React Router DOM 7.12.0
- **Styling:** Tailwind CSS 4.1.18
- **HTTP Client:** Axios 1.13.2
- **Icons:** Lucide React 0.562.0

### Infrastructure
- **Containerization:** Docker & Docker Compose

## Features

- **Authentication & Authorization**: Secure login, registration, and token refresh using JWT with automatic token rotation.
- **Task Management**: Create, read, update, and delete tasks with priority levels and due dates.
- **Project Organization**: Group tasks into projects for better organization.
- **Tagging System**: Organize tasks with custom tags for easy filtering.
- **Recurring Tasks**: Support for repeating tasks (daily, weekly, monthly, yearly) with automatic generation.
- **Sub-tasks**: Create hierarchical task structures with parent-child relationships.
- **Reminders**: Notification system for upcoming deadlines with scheduled reminders.
- **Statistics Dashboard**: Real-time overview of tasks, projects, and tags.
- **Modern UI**: Clean, responsive interface built with React and Tailwind CSS.

## Prerequisites

Before you begin, ensure you have the following installed:
- **Java 21** (for backend)
- **Node.js 20+** (for frontend)
- **Docker & Docker Compose** (for database)
- **Maven** (optional, wrapper included)

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/hoanghonghuy/taskly.git
cd taskly-app
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=taskly_db

# Backend
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_secret_key_here_at_least_32_bytes

# Frontend
VITE_API_BASE_URL=http://localhost:8080
```

### 3. Start the Database

Use Docker Compose to spin up the PostgreSQL database:

```bash
docker-compose up -d
```

### 4. Run the Backend

Navigate to the backend directory and run the application:

```bash
cd backend/taskly

# Linux/macOS
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

The backend will start on `http://localhost:8080`

### 5. Run the Frontend

In a new terminal, navigate to the frontend directory and start the development server:

```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

## Project Structure

```
taskly-app/
├── backend/taskly/              # Spring Boot Backend
│   ├── src/main/java/
│   │   └── io/github/hoanghonghuy/taskly/
│   │       ├── config/          # Security & JWT configuration
│   │       ├── controller/      # REST API endpoints
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── entity/          # JPA entities (Task, Project, Tag, User)
│   │       ├── exception/       # Global exception handling
│   │       ├── repository/      # JPA repositories
│   │       └── service/         # Business logic
│   ├── src/main/resources/
│   │   └── application.properties  # Application configuration
│   └── pom.xml                  # Maven dependencies
├── frontend/                    # React Frontend
│   ├── src/
│   │   ├── components/          # Reusable components (Modal, ConfirmDialog)
│   │   ├── contexts/            # React Context (AuthContext)
│   │   ├── hooks/               # Custom hooks (useAuth, useCRUD, useStats)
│   │   ├── lib/                 # API clients and utilities
│   │   ├── pages/               # Page components (Dashboard, Login, Tasks, etc.)
│   │   └── types/               # TypeScript type definitions
│   ├── public/                  # Static assets
│   └── package.json             # Node.js dependencies
├── docker-compose.yml           # Database configuration
└── README.md                    # This file
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/refresh` - Refresh access token

### Tasks
- `GET /api/tasks` - Get all tasks for authenticated user
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### Projects
- `GET /api/projects` - Get all projects
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Tags
- `GET /api/tags` - Get all tags
- `POST /api/tags` - Create new tag
- `DELETE /api/tags/{id}` - Delete tag

### Statistics
- `GET /api/stats` - Get user statistics

## Development

### Backend Development

The backend uses Spring Boot with:
- JPA/Hibernate for database operations
- Spring Security for authentication
- JWT for stateless authentication
- Scheduled tasks for recurring tasks and reminders

Run tests:
```bash
cd backend/taskly
./mvnw test
```

### Frontend Development

The frontend uses React with:
- TypeScript for type safety
- React Router for navigation
- Tailwind CSS for styling
- Axios for API calls with automatic token refresh

Build for production:
```bash
cd frontend
npm run build
```

Run linter:
```bash
npm run lint
```

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License.
