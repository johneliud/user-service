# user-service

Microservice where users can register as clients or sellers, with sellers having the option to upload an avatar.

## Completed Features

- US-1: Database Schema Design & Implementation
  - User model with MongoDB document mapping
  - Role enum (CLIENT/SELLER)
  - Unique email index
  - Input validation constraints
  - UserRepository with email lookup methods

- US-2: User Registration API
  - POST /api/users/register endpoint
  - Role selection support (CLIENT/SELLER)
  - Password hashing with BCrypt
  - Email format and password strength validation
  - Duplicate email detection
  - JSON response format
  - Comprehensive logging

- US-3: Avatar Upload for Sellers
  - Avatar upload during registration (sellers only)
  - File type validation (PNG, JPG, JPEG, WEBP)
  - File size limit (2MB)
  - PUT /api/users/profile/avatar endpoint
  - Secure file storage with UUID naming

- US-4: Authentication Implementation
  - POST /api/users/login endpoint
  - JWT token generation with user claims
  - Password verification with BCrypt
  - Token includes user ID, email, and role
  - JSON response with token and user data

- US-5: Profile Management
  - GET /api/users/profile endpoint (authenticated)
  - PUT /api/users/profile endpoint for updates
  - JWT-based authentication required
  - Users can only access their own profile
  - Password never exposed in responses