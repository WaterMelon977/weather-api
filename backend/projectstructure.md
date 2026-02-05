# Project Structure

This document provides an overview of the directory structure for the Weather API project.

```text
weather-api/
├── .agent/                 # Agent-specific configurations and skills
├── backend/                # Spring Boot Backend
│   ├── .mvn/               # Maven wrapper configuration
│   ├── src/                # Source code
│   │   ├── main/
│   │   │   ├── java/com/rdbackend/weather_api/
│   │   │   │   ├── client/      # External API clients
│   │   │   │   ├── config/      # Configuration classes (Security, Redis, etc.)
│   │   │   │   ├── controller/  # REST Controllers
│   │   │   │   ├── dto/         # Data Transfer Objects
│   │   │   │   ├── entity/      # JPA Entities
│   │   │   │   ├── exception/   # Custom Exception handlers
│   │   │   │   ├── repo/        # Spring Data JPA Repositories
│   │   │   │   ├── security/    # JWT and OAuth2 security logic
│   │   │   │   ├── service/     # Business logic services
│   │   │   │   └── WeatherApiApplication.java
│   │   │   └── resources/
│   │   │       ├── application.yaml          # Main configuration (gitignored)
│   │   │       ├── application.yml.example   # Template for configuration
│   │   │       ├── static/                   # Static assets
│   │   │       └── templates/                # Template files
│   │   └── test/           # Unit and Integration tests
│   ├── .dockerignore       # Docker build exclusions
│   ├── .gitignore          # Git exclusions
│   ├── Dockerfile          # Container definition
│   ├── mvnw                # Maven wrapper script (Unix)
│   ├── mvnw.cmd            # Maven wrapper script (Windows)
│   ├── pom.xml             # Maven Project Object Model
│   └── DOCKER_TROUBLESHOOTING.md
├── frontend/               # Next.js Frontend
│   └── ...                 # (Next.js specific structure)
├── README.md               # Main project documentation
└── LICENSE                 # Project license
```

## Key Files (Backend)

| File | Description |
|      |             |
| `Dockerfile` | Multi-stage build for the Spring Boot application using Java 21. |
| `pom.xml` | Defines project dependencies (Spring Boot, OAuth2, JWT, JPA, Redis). |
| `.gitignore` | Configured to ignore sensitive `application.yaml` and OS-specific files. |
| `.dockerignore` | Optimized to keep the Docker build context clean and fast. |
| `src/main/resources/application.yml.example` | Provides a blueprint for required environment variables and secrets. |
