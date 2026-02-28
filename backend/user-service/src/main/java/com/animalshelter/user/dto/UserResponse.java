package com.animalshelter.user.dto;

import com.animalshelter.user.model.User;
import com.animalshelter.user.model.UserRole;
import com.animalshelter.user.model.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    UserRole role,
    UserStatus status,
    LocalDateTime createdAt
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }
}
