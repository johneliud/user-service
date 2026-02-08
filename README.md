# User Service

A Spring Boot microservice for user management with role-based authentication, supporting client and seller registration with optional avatar uploads for sellers.

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)

## Features

### Completed Implementations

#### US-1: Database Schema Design & Implementation
- User model with MongoDB document mapping
- Role enum (CLIENT/SELLER)
- Unique email index
- Input validation constraints
- UserRepository with email lookup methods

#### US-2: User Registration API
- POST /api/users/register endpoint
- Role selection support (CLIENT/SELLER)
- Password hashing with BCrypt
- Email format and password strength validation
- Duplicate email detection
- JSON response format
- Comprehensive logging

#### US-3: Avatar Upload for Sellers
- Avatar upload during registration (sellers only)
- File type validation (PNG, JPG, JPEG, WEBP)
- MIME type verification
- File size limit (2MB)
- PUT /api/users/profile/avatar endpoint
- Secure file storage with UUID naming

#### US-4: Authentication Implementation
- POST /api/users/login endpoint
- JWT token generation with user claims
- Password verification with BCrypt
- Token includes user ID, email, and role
- JSON response with token and user data

#### US-5: Profile Management
- GET /api/users/profile endpoint (authenticated)
- PUT /api/users/profile endpoint for updates
- JWT-based authentication required
- Users can only access their own profile
- Password never exposed in responses

#### US-6: Role-Based Authorization
- Method-level security with @PreAuthorize
- ROLE_SELLER required for avatar uploads
- JWT filter extracts and sets user roles
- Access denied handling with proper error responses

#### US-7: Security Measures
- Rate limiting with Bucket4j (5 login attempts per 15 minutes per IP)
- Password complexity validation (uppercase, lowercase, digit, special character)
- CORS configuration for frontend origins
- Security headers (CSP, Frame Options)

#### US-8: Error Handling & Validation
- Global exception handler
- Comprehensive input validation for all endpoints
- Consistent error response format
- Edge case handling (invalid credentials, rate limits, etc.)

#### US-9: Unit & Integration Testing
- Unit tests for service layer
- Authentication flow tests
- Password hashing validation tests

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.2**
- **MongoDB** - Database
- **Spring Security** - Authentication & Authorization
- **JWT (JJWT 0.12.5)** - Token-based authentication
- **Bucket4j 8.10.1** - Rate limiting
- **Lombok** - Boilerplate reduction
- **Maven** - Build tool

## Getting Started

### Prerequisites

- Java 25 or higher
- Maven 3.6+
- MongoDB Atlas account or local MongoDB instance

### Clone Repository

```bash
git clone https://github.com/johneliud/user-service.git
cd user-service
```

### Configuration

Create and Update `src/main/resources/application-secrets.properties`:

```properties
spring.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<database>
# Server Configuration
server.port=8080
# JWT Configuration
jwt.secret=<your-secret-key>
jwt.expiration=86400000
# Rate limiting configuration
rate.limit.login.capacity=5
rate.limit.login.refill.tokens=5
rate.limit.login.refill.minutes=15
# File upload configuration
spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=2MB
file.upload.dir=uploads/avatars
```

### Build & Run

```bash
# Build
./mvnw clean install

# Run
./mvnw spring-boot:run
```

The service will start on `http://localhost:8080`

### Run Tests

```bash
./mvnw test
```

## API Documentation

See [API_TESTING.md](docs/API_TESTING.md) for detailed endpoint documentation with Postman/Insomnia examples.

## Testing

The project includes:
- Unit tests for service layer
- Authentication and authorization tests
- Password validation tests

Run tests with: `./mvnw test`