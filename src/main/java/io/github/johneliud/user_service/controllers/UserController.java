package io.github.johneliud.user_service.controllers;

import io.github.johneliud.user_service.config.RateLimitService;
import io.github.johneliud.user_service.dto.ApiResponse;
import io.github.johneliud.user_service.dto.LoginRequest;
import io.github.johneliud.user_service.dto.LoginResponse;
import io.github.johneliud.user_service.dto.RegisterRequest;
import io.github.johneliud.user_service.dto.UpdateProfileRequest;
import io.github.johneliud.user_service.dto.UserResponse;
import io.github.johneliud.user_service.services.AuthService;
import io.github.johneliud.user_service.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final RateLimitService rateLimitService;

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
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        String clientIp = getClientIP(httpRequest);
        String rateLimitKey = "login:" + clientIp;
        
        if (!rateLimitService.tryConsume(rateLimitKey)) {
            log.warn("POST /api/users/login - Rate limit exceeded for IP: {}", clientIp);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiResponse<>(false, "Too many login attempts. Please try again later.", null));
        }
        
        log.info("POST /api/users/login - Login request received for email: {}", request.getEmail());
        
        LoginResponse loginResponse = authService.login(request);
        
        log.info("POST /api/users/login - Login successful for user: {}", loginResponse.getUser().getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", loginResponse));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        log.info("GET /api/users/profile - Profile request for user: {}", userId);
        
        UserResponse userResponse = userService.getProfile(userId);
        
        log.info("GET /api/users/profile - Profile retrieved for user: {}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved successfully", userResponse));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        String userId = (String) authentication.getPrincipal();
        log.info("PUT /api/users/profile - Profile update request for user: {}", userId);
        
        UserResponse userResponse = userService.updateProfile(userId, request);
        
        log.info("PUT /api/users/profile - Profile updated for user: {}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", userResponse));
    }

    @PutMapping("/profile/avatar")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(
            Authentication authentication,
            @RequestPart("avatar") MultipartFile avatar) {
        
        String userId = (String) authentication.getPrincipal();
        log.info("PUT /api/users/profile/avatar - Avatar update request for user: {}", userId);
        
        UserResponse userResponse = userService.updateAvatar(userId, avatar);
        
        log.info("PUT /api/users/profile/avatar - Avatar updated successfully for user: {}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Avatar updated successfully", userResponse));
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
