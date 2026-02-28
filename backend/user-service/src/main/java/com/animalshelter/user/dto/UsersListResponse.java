package com.animalshelter.user.dto;

import java.util.List;

public record UsersListResponse(
    boolean success,
    List<UserResponse> users,
    long total
) {}
