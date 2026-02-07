package io.github.johneliud.user_service.dto;

import io.github.johneliud.user_service.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private String avatar;
}
