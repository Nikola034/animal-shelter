package com.animalshelter.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(max = 100, message = "Name must be at most 100 characters")
    String name,

    @Email(message = "Invalid email format")
    String email
) {}
