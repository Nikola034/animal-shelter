package com.animalshelter.user.dto;

public record AuthResponse(
    boolean success,
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserResponse user
) {}
