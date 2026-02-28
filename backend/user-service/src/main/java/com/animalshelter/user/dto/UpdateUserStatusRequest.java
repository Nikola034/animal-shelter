package com.animalshelter.user.dto;

import com.animalshelter.user.model.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
    @NotNull(message = "Status is required")
    UserStatus status
) {}
