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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/users/register - Registration request received for email: {}", request.getEmail());
        
        UserResponse userResponse = userService.registerUser(request);
        
        log.info("POST /api/users/register - User registered successfully: {}", userResponse.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "User registered successfully", userResponse));
    }
}
