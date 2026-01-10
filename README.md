# Taskly - Task Management Application

Taskly is a robust and scalable task management REST API built with Java and Spring Boot. It provides comprehensive features for managing tasks, projects, and tags with secure authentication using JWT.

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.5.9
- **Database:** PostgreSQL 16
- **Build Tool:** Maven
- **Security:** Spring Security, OAuth2 Resource Server (JWT)
- **Containerization:** Docker & Docker Compose
- **Tools:** Lombok

## Features

- **Authentication & Authorization**: Secure login, registration, and token refresh using JWT.
- **Task Management**: Create, read, update, and delete tasks.
- **Project Organization**: Group tasks into projects.
- **Tagging System**: Organize tasks with custom tags.
- **Recurring Tasks**: Support for repeating tasks.
- **Reminders**: Notification system for upcoming deadlines.
- **Prioritization**: Set priority levels for tasks.

## Prerequisites

Before you begin, ensure you have the following installed:
- Java 21
- Docker & Docker Compose
- Maven (optional, wrapper included)

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/hoanghonghuy/taskly.git
cd taskly
```

### 2. Configure Environment Variables

The application requires certain environment variables to run. You can set them in your IDE or export them in your terminal.

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | *(empty)* |
| `JWT_SECRET` | Secret key for signing JWTs | *(must be provided)* |
| `POSTGRES_PASSWORD` | Password for Docker Postgres | *(must be provided)* |

### 3. Start the Database

Use Docker Compose to spin up the PostgreSQL database.

```bash
# Create a .env file for docker-compose if needed, or export the variable
export POSTGRES_PASSWORD=mysecretpassword

docker-compose up -d
```

### 4. Run the Application

Navigate to the backend directory and run the application using the Maven wrapper.

```bash
cd backend/taskly

# Linux/macOS
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.password=mysecretpassword --app.jwt.secret=your_very_long_secret_key_here_at_least_32_bytes"

# Windows
.\mvnw.cmd spring-boot:run
```

*Note: Ensure the `spring.datasource.password` matches what you set in `POSTGRES_PASSWORD`.*

## Project Structure

```
taskly/
├── backend/taskly/         # Spring Boot Backend
│   ├── src/main/java/      # Source code
│   └── src/main/resources/ # Config & properties
└── docker-compose.yml      # Database configuration
```

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License.