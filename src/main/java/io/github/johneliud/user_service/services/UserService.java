package io.github.johneliud.user_service.services;

import io.github.johneliud.user_service.dto.RegisterRequest;
import io.github.johneliud.user_service.dto.UpdateProfileRequest;
import io.github.johneliud.user_service.dto.UserResponse;
import io.github.johneliud.user_service.models.Role;
import io.github.johneliud.user_service.models.User;
import io.github.johneliud.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public UserResponse registerUser(RegisterRequest request, MultipartFile avatar) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        if (avatar != null && !avatar.isEmpty() && request.getRole() != Role.SELLER) {
            log.warn("Registration failed: Only sellers can upload avatars");
            throw new IllegalArgumentException("Only sellers can upload avatars");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        if (avatar != null && !avatar.isEmpty()) {
            String avatarPath = fileStorageService.storeAvatar(avatar);
            user.setAvatar(avatarPath);
            log.info("Avatar uploaded for user: {}", avatarPath);
        }

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {} and role: {}", savedUser.getId(), savedUser.getRole());

        return toUserResponse(savedUser);
    }

    public UserResponse getProfile(String userId) {
        log.info("Fetching profile for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Profile fetch failed: User not found - {}", userId);
                    return new IllegalArgumentException("User not found");
                });
        
        log.info("Profile fetched successfully for user: {}", userId);
        return toUserResponse(user);
    }

    public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Profile update failed: User not found - {}", userId);
                    return new IllegalArgumentException("User not found");
                });

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", userId);
        
        return toUserResponse(updatedUser);
    }

    public UserResponse updateAvatar(String userId, MultipartFile avatar) {
        log.info("Attempting to update avatar for user: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("Avatar update failed: User not found - {}", userId);
                return new IllegalArgumentException("User not found");
            });

        if (user.getRole() != Role.SELLER) {
            log.warn("Avatar update failed: User is not a seller - {}", userId);
            throw new IllegalArgumentException("Only sellers can upload avatars");
        }

        if (user.getAvatar() != null) {
            fileStorageService.deleteAvatar(user.getAvatar());
        }

        String avatarPath = fileStorageService.storeAvatar(avatar);
        user.setAvatar(avatarPath);
        User updatedUser = userRepository.save(user);

        log.info("Avatar updated successfully for user: {}", userId);
        return toUserResponse(updatedUser);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getAvatar()
        );
    }
}

