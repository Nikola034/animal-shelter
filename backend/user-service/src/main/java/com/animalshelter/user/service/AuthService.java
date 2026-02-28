package com.animalshelter.user.service;

import com.animalshelter.user.config.JwtUtils;
import com.animalshelter.user.dto.*;
import com.animalshelter.user.exception.AuthenticationException;
import com.animalshelter.user.exception.DuplicateResourceException;
import com.animalshelter.user.exception.InvalidTokenException;
import com.animalshelter.user.model.RefreshToken;
import com.animalshelter.user.model.User;
import com.animalshelter.user.model.UserStatus;
import com.animalshelter.user.repository.RefreshTokenRepository;
import com.animalshelter.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        if (user.getStatus() == UserStatus.Pending) {
            throw new AuthenticationException("Your account is pending approval by an administrator");
        }

        if (user.getStatus() == UserStatus.Inactive) {
            throw new AuthenticationException("Your account has been deactivated");
        }

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshTokenStr = jwtUtils.generateRefreshToken(user);

        // Delete any existing refresh tokens for this user
        refreshTokenRepository.deleteByUser(user);

        // Create new refresh token entity
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtUtils.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                true,
                accessToken,
                refreshTokenStr,
                "Bearer",
                jwtUtils.getAccessTokenExpiration(),
                UserResponse.fromEntity(user)
        );
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .status(UserStatus.Pending)
                .build();

        user = userRepository.save(user);

        return new RegisterResponse(
                true,
                "Registration successful. Your account is pending approval by an administrator.",
                UserResponse.fromEntity(user)
        );
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new InvalidTokenException("Refresh token has expired. Please log in again.");
        }

        User user = storedToken.getUser();

        if (user.getStatus() != UserStatus.Active) {
            refreshTokenRepository.delete(storedToken);
            throw new AuthenticationException("Your account is no longer active");
        }

        // Rotate: delete old token, create new ones
        refreshTokenRepository.delete(storedToken);

        String newAccessToken = jwtUtils.generateAccessToken(user);
        String newRefreshTokenStr = jwtUtils.generateRefreshToken(user);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenStr)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtUtils.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshTokenStr,
                "Bearer",
                jwtUtils.getAccessTokenExpiration()
        );
    }

    @Transactional
    public MessageResponse logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        refreshTokenRepository.deleteByUser(user);

        return new MessageResponse(true, "Logged out successfully");
    }
}
