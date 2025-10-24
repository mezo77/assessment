# Device Management API

A Spring Boot application providing a REST API for managing device resources.

## Overview

This project implements a RESTful API for creating, reading, updating, and deleting device resources. It uses Spring Boot 3, Java 21, Maven, PostgreSQL for persistence, and includes Swagger for API documentation.

## Features

- **CRUD Operations**: Create, read, update (full and partial), and delete devices.
- **Filtering**: Fetch devices by brand or state.
- **Validation**: Business rules enforced (e.g., cannot update name/brand of in-use devices, cannot delete in-use devices).
- **API Documentation**: Swagger UI available at `/swagger-ui.html`.
- **Containerization**: Docker support for easy deployment.
- **Testing**: Unit tests and integration tests with Testcontainers.

## Domain Model

Device:
- `id` (Long): Unique identifier.
- `name` (String): Device name (required, cannot be updated if device is in-use).
- `brand` (String): Device brand (required, cannot be updated if device is in-use).
- `state` (Enum): AVAILABLE, IN_USE, INACTIVE.
- `creationTime` (LocalDateTime): Auto-set on creation, cannot be updated.

## API Endpoints

### Base URL: `/api/v1/devices`

- `POST /api/v1/devices` - Create a new device.
- `GET /api/v1/devices` - Get all devices (paginated).
- `GET /api/v1/devices/{id}` - Get device by ID.
- `PUT /api/v1/devices/{id}` - Fully update a device.
- `PATCH /api/v1/devices/{id}` - Partially update a device.
- `DELETE /api/v1/devices/{id}` - Delete a device.
- `GET /api/v1/devices/brand/{brand}` - Get devices by brand.
- `GET /api/v1/devices/state/{state}` - Get devices by state (AVAILABLE, IN_USE, INACTIVE).

### Response Structure

Example response for `GET /api/v1/devices`:
```json
{
    "content": [
        {
            "id": 1,
            "name": "Device 1",
            "brand": "Brand A",
            "state": "AVAILABLE",
            "creationTime": "2025-10-24T10:00:00"
        },
        {
            "id": 2,
            "name": "Device 2",
            "brand": "Brand B",
            "state": "IN_USE",
            "creationTime": "2025-10-24T11:00:00"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 20,
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        }
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "first": true,
    "numberOfElements": 2,
    "size": 20,
    "number": 0
}
```

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
- Name and brand cannot be updated if the device state is IN_USE.
- Devices with state IN_USE cannot be deleted.

## Future Improvements

- Add authentication and authorization.
- Add more advanced filtering and sorting.
- Use DTOs for different operations (e.g., separate CreateDeviceRequest).
- Add caching for better performance.
- Implement event-driven architecture for device state changes.
- Add monitoring.

## Project Structure

- `src/main/java/com/example/assessment/controller/` - REST controllers.
- `src/main/java/com/example/assessment/service/` - Business logic.
- `src/main/java/com/example/assessment/repository/` - Data access.
- `src/main/java/com/example/assessment/model/` - Domain models.
- `src/test/java/` - Tests.

## License

No license specified.
