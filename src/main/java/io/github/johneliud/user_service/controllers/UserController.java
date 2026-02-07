package io.github.johneliud.user_service.controllers;

import io.github.johneliud.user_service.dto.ApiResponse;
import io.github.johneliud.user_service.dto.RegisterRequest;
import io.github.johneliud.user_service.dto.UserResponse;
import io.github.johneliud.user_service.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

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

    @PutMapping("/profile/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(
            @RequestParam("userId") String userId,
            @RequestPart("avatar") MultipartFile avatar) {
        
        log.info("PUT /api/users/profile/avatar - Avatar update request for user: {}", userId);
        
        UserResponse userResponse = userService.updateAvatar(userId, avatar);
        
        log.info("PUT /api/users/profile/avatar - Avatar updated successfully for user: {}", userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Avatar updated successfully", userResponse));
    }
}
