package io.github.johneliud.user_service.controllers;

import io.github.johneliud.user_service.dto.ApiResponse;
import io.github.johneliud.user_service.dto.LoginRequest;
import io.github.johneliud.user_service.dto.LoginResponse;
import io.github.johneliud.user_service.dto.RegisterRequest;
import io.github.johneliud.user_service.dto.UpdateProfileRequest;
import io.github.johneliud.user_service.dto.UserResponse;
import io.github.johneliud.user_service.services.AuthService;
import io.github.johneliud.user_service.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestPart("user") RegisterRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        
        log.info("POST /api/users/register - Registration request received for email: {}", request.getEmail());
        
        UserResponse userResponse = userService.registerUser(request, avatar);
        
        log.info("POST /api/users/register - User registered successfully: {}", userResponse.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "User registered successfully", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/users/login - Login request received for email: {}", request.getEmail());
        
        LoginResponse loginResponse = authService.login(request);
        
        log.info("POST /api/users/login - Login successful for user: {}", loginResponse.getUser().getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", loginResponse));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/users/profile - Profile request for user: {}", userId);
        
        UserResponse userResponse = userService.getProfile(userId);
        
        log.info("GET /api/users/profile - Profile retrieved for user: {}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", userResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        log.info("GET /api/users/{} - Get user by ID request", id);
        
        UserResponse userResponse = userService.getProfile(id);
        
        log.info("GET /api/users/{} - User retrieved successfully", id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", userResponse));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        log.info("PUT /api/users/profile - Profile update request for user: {}", userId);
        
        UserResponse userResponse = userService.updateProfile(userId, request);
        
        log.info("PUT /api/users/profile - Profile updated for user: {}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", userResponse));
    }

    @PutMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestPart("avatar") MultipartFile avatar) {
        
        if (!role.equals("SELLER")) {
            throw new IllegalArgumentException("Only sellers can update avatar");
        }
        
        log.info("PUT /api/users/profile/avatar - Avatar update request for user: {}", userId);
        
        UserResponse userResponse = userService.updateAvatar(userId, avatar);
        
        log.info("PUT /api/users/profile/avatar - Avatar updated successfully for user: {}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Avatar updated successfully", userResponse));
    }

    @GetMapping("/avatars/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/avatars").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            String contentType = determineContentType(filename);
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
                .body(resource);
        } catch (Exception e) {
            log.error("Error retrieving avatar: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}
