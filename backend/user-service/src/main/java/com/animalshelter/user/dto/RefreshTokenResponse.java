package com.animalshelter.user.dto;

public record RefreshTokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn
) {}
