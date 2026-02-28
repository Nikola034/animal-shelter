package com.animalshelter.user.dto;

public record RegisterResponse(
    boolean success,
    String message,
    UserResponse user
) {}
