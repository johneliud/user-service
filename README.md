# User Service

Microservice responsible for user management, authentication, and profile operations.

## Overview

- **Port**: 8080
- **Technology**: Spring Boot 3.x
- **Database**: MongoDB collection `users`
- **Purpose**: User registration, authentication, and profile management

## Features

### User Registration
- Register as CLIENT or SELLER
- Password encryption with BCrypt
- Email validation
- Avatar upload for sellers (optional)

### Authentication
- JWT token generation
- Password verification
- Token expiration: 24 hours

### Profile Management
- View user profile
- Update name and email
- Upload/update avatar (sellers only)
- Serve avatar images

### Avatar Management
- Upload avatar images (PNG, JPG, JPEG, WEBP)
- Max file size: 2MB
- Stored in `uploads/avatars/`
- Served via `/api/users/avatars/{filename}`

## API Endpoints

### Public Endpoints

#### Register User
```http
POST /api/users/register
Content-Type: multipart/form-data

user: {
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "SELLER"
}
avatar: <file> (optional, sellers only)
```

#### Login
```http
POST /api/users/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGc...",
    "user": {
      "id": "...",
      "name": "John Doe",
      "email": "john@example.com",
      "role": "SELLER",
      "avatar": "filename.png"
    }
  }
}
```

### Protected Endpoints

Require `Authorization: Bearer <token>` header and X-User-Id header (added by gateway).

#### Get Profile
```http
GET /api/users/profile
```

#### Update Profile
```http
PUT /api/users/profile
Content-Type: application/json

{
  "name": "John Updated",
  "email": "john.new@example.com"
}
```

#### Update Avatar (Sellers Only)
```http
PUT /api/users/profile/avatar
Content-Type: multipart/form-data

avatar: <file>
```

#### Get Avatar Image
```http
GET /api/users/avatars/{filename}
```

Returns image with appropriate Content-Type.

## Data Model

### User
```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "password": "string (BCrypt hashed)",
  "role": "CLIENT | SELLER",
  "avatar": "string (filename, optional)"
}
```

## Configuration

### Application Properties
```properties
server.port=8080
spring.data.mongodb.uri=mongodb://localhost:27017/buy01
jwt.secret=your-secret-key
jwt.expiration=86400000
spring.servlet.multipart.max-file-size=2MB
```

## Running the Service

```bash
cd backend/user-service
mvn spring-boot:run
```

Ensure MongoDB is running on port 27017.

## File Storage

Avatars are stored in:
```
backend/user-service/uploads/avatars/
```

Files are named with UUID: `{uuid}.{extension}`

## Security

- Passwords hashed with BCrypt (strength 10)
- JWT tokens signed with secret key
- Avatar upload restricted to sellers
- File type validation (images only)
- File size validation (max 2MB)

## Dependencies

- Spring Boot 3.x
- Spring Data MongoDB
- Spring Security
- JWT (io.jsonwebtoken)
- BCrypt
- Lombok

## Error Responses

```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

Common errors:
- 400 - Invalid request data
- 401 - Invalid credentials
- 403 - Insufficient permissions
- 404 - User not found
