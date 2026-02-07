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