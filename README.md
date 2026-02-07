# user-service

Microservice where users can register as clients or sellers, with sellers having the option to upload an avatar.

## Completed Features

- US-1: Database Schema Design & Implementation
  - User model with MongoDB document mapping
  - Role enum (CLIENT/SELLER)
  - Unique email index
  - Input validation constraints
  - UserRepository with email lookup methods