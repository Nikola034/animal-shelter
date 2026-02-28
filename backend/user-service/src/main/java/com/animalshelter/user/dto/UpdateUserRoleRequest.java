package com.animalshelter.user.dto;

import com.animalshelter.user.model.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
    @NotNull(message = "Role is required")
    UserRole role
) {}
