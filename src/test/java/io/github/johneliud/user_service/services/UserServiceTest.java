package io.github.johneliud.user_service.services;

import io.github.johneliud.user_service.dto.RegisterRequest;
import io.github.johneliud.user_service.dto.UserResponse;
import io.github.johneliud.user_service.models.Role;
import io.github.johneliud.user_service.models.User;
import io.github.johneliud.user_service.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123!");
        request.setRole(Role.CLIENT);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        
        User savedUser = new User();
        savedUser.setId("123");
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setRole(request.getRole());
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.registerUser(request, null);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(Role.CLIENT, response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request, null));
        verify(userRepository, never()).save(any(User.class));
    }
}
