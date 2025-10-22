# Assessment Project

A Spring Boot application providing a REST API for managing deviceDTO resources.

## Overview

This project implements a RESTful API for creating, reading, updating, and deleting deviceDTO resources. It uses Spring Boot 3, Java 21, Maven, PostgreSQL for persistence, and includes Swagger for API documentation.

## Features

- **CRUD Operations**: Create, read, update (full and partial), and delete deviceDTOS.
- **Filtering**: Fetch deviceDTOS by brand or state.
- **Validation**: Business rules enforced (e.g., cannot update name/brand of in-use deviceDTOS, cannot delete in-use deviceDTOS).
- **API Documentation**: Swagger UI available at `/swagger-ui.html`.
- **Containerization**: Docker support for easy deployment.
- **Testing**: Unit tests and integration tests with Testcontainers.

## Domain Model

Device:
- `id` (Long): Unique identifier.
- `name` (String): Device name (required, cannot be updated if deviceDTO is in-use).
- `brand` (String): Device brand (required, cannot be updated if deviceDTO is in-use).
- `state` (Enum): AVAILABLE, IN_USE, INACTIVE.
- `creationTime` (LocalDateTime): Auto-set on creation, cannot be updated.

## API Endpoints

### Base URL: `/deviceDTOS`

- `POST /deviceDTOS` - Create a new deviceDTO.
- `GET /deviceDTOS` - Get all deviceDTOS.
- `GET /deviceDTOS/{id}` - Get deviceDTO by ID.
- `PUT /deviceDTOS/{id}` - Fully update a deviceDTO.
- `PATCH /deviceDTOS/{id}` - Partially update a deviceDTO.
- `DELETE /deviceDTOS/{id}` - Delete a deviceDTO.
- `GET /deviceDTOS/brand/{brand}` - Get deviceDTOS by brand.
- `GET /deviceDTOS/state/{state}` - Get deviceDTOS by state (AVAILABLE, IN_USE, INACTIVE).

## Prerequisites

- Java 21
- Maven 3.9+
- Docker (for running PostgreSQL and containerization)

## Setup and Run

### Local Development

1. Start PostgreSQL:
   ```bash
   docker-compose up -d db
   ```

2. Build the application:
   ```bash
   ./mvnw clean package
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`.

### Using Docker

1. Build the Docker image:
   ```bash
   docker build -t assessment .
   ```

2. Run with Docker Compose (includes DB):
   ```bash
   docker-compose up
   ```

## Configuration

Database configuration is in `src/main/resources/application.properties`. For production, use environment variables.

## Testing

Run unit and integration tests:
```bash
./mvnw test
```

Integration tests use Testcontainers for PostgreSQL.

## API Documentation

Access Swagger UI at `http://localhost:8080/swagger-ui.html` after starting the application.

## Validation Rules

- Creation time is set automatically and cannot be modified.
- Name and brand cannot be updated if the deviceDTO state is IN_USE.
- Devices with state IN_USE cannot be deleted.

## Future Improvements

- Add authentication and authorization.
- Implement pagination for list endpoints.
- Add more advanced filtering and sorting.
- Use DTOs for different operations (e.g., separate CreateDeviceRequest).
- Add caching for better performance.
- Implement event-driven architecture for deviceDTO state changes.
- Add monitoring and logging.

## Project Structure

- `src/main/java/com/example/assessment/controller/` - REST controllers.
- `src/main/java/com/example/assessment/service/` - Business logic.
- `src/main/java/com/example/assessment/repository/` - Data access.
- `src/main/java/com/example/assessment/model/` - Domain models.
- `src/test/java/` - Tests.

## License

No license specified.
