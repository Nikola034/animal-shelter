package com.animalshelter.user.service;

import com.animalshelter.user.dto.*;
import com.animalshelter.user.exception.ResourceNotFoundException;
import com.animalshelter.user.model.User;
import com.animalshelter.user.model.UserStatus;
import com.animalshelter.user.repository.RefreshTokenRepository;
import com.animalshelter.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public UserResponse getCurrentUser(String userId) {
        UUID id = UUID.fromString(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromEntity(user);
    }

    public UsersListResponse getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromEntity)
                .toList();
        return new UsersListResponse(true, userResponses, userResponses.size());
    }

    public UsersListResponse getUsersByStatus(UserStatus status) {
        List<User> users = userRepository.findByStatus(status);
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::fromEntity)
                .toList();
        return new UsersListResponse(true, userResponses, userResponses.size());
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateUserStatus(UUID id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(request.status());
        user = userRepository.save(user);

        // If user is deactivated, revoke their refresh tokens
        if (request.status() == UserStatus.Inactive) {
            refreshTokenRepository.deleteByUser(user);
        }

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse updateUserRole(UUID id, UpdateUserRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setRole(request.role());
        user = userRepository.save(user);

        return UserResponse.fromEntity(user);
    }

    @Transactional
    public MessageResponse deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        refreshTokenRepository.deleteByUser(user);
        userRepository.delete(user);

        return new MessageResponse(true, "User deleted successfully");
    }
}
