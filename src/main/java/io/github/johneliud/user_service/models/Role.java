package io.github.johneliud.user_service.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    CLIENT,
    SELLER;

    @JsonCreator
    public static Role fromString(String value) {
        return Role.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
