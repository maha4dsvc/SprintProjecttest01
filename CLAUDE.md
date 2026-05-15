# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build (compile only)
mvn compile -B --no-transfer-progress

# Run tests
mvn test -B --no-transfer-progress

# Run a single test class
mvn test -Dtest=LoginAppApplicationTests -B --no-transfer-progress

# Package JAR (skip tests)
mvn package -DskipTests -B --no-transfer-progress

# Run the app locally
mvn spring-boot:run

# Build Docker image locally
docker build -t loginapp .

# Run the Docker container locally
docker run -d --name loginapp -p 8080:8080 loginapp
```

App runs on `http://localhost:8080`. Default credentials: `admin` / `password`.

## Architecture

Spring Boot 3.2.5 / Java 17 web app with form-based authentication and Thymeleaf templates.

**Authentication flow:**
- `SecurityConfig` defines the filter chain — `/register` is public, everything else requires authentication. Login page is `/login`, success redirects to `/home`.
- `CustomUserDetailsService` implements Spring Security's `UserDetailsService` by delegating to `UserStore`.
- `UserStore` is an in-memory `ConcurrentHashMap` (no database). It seeds one user (`admin`/`password`) on startup. All registered users are lost when the app restarts.
- Passwords are BCrypt-encoded via the `PasswordEncoder` bean defined in `SecurityConfig`.

**Registration flow:**
- `RegistrationController` handles `GET /register` (form) and `POST /register` (submission). Validation (blank fields, password match, min length, duplicate username) happens in the controller before writing to `UserStore`.

**CI/CD pipeline** (`.github/workflows/ci-cd.yml`) — 6 sequential stages:
1. **Compile** — `mvn compile`
2. **Security Checks** — Gitleaks secret scan + Trivy filesystem/dependency scan (report-only, non-blocking)
3. **Test** — `mvn test`, uploads Surefire reports
4. **Package** — `mvn package -DskipTests`, uploads JAR artifact
5. **Docker** — builds and pushes image to DockerHub as `DOCKERHUB_USERNAME/sprintprojecttest01:latest` and `:<sha>`, then scans image with Trivy (push events on master/main only)
6. **Deploy** — pulls and runs the container, smoke-tests `GET /login` for HTTP 200/302 (push events on master/main only)

Required GitHub secrets: `DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`.

The Dockerfile uses `eclipse-temurin:17-jre-alpine` and expects the JAR at `target/*.jar`.
