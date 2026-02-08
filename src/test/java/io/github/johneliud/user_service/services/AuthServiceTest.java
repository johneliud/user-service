package io.github.johneliud.user_service.services;

import io.github.johneliud.user_service.dto.LoginRequest;
import io.github.johneliud.user_service.dto.LoginResponse;
import io.github.johneliud.user_service.models.Role;
import io.github.johneliud.user_service.models.User;
import io.github.johneliud.user_service.repositories.UserRepository;
import io.github.johneliud.user_service.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password123!");

        User user = new User();
        user.setId("123");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.CLIENT);
        user.setName("Test User");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name())).thenReturn("token123");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("Test User", response.getUser().getName());
    }

    @Test
    void login_InvalidEmail_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("Password123!");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("WrongPassword");

        User user = new User();
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }
}
