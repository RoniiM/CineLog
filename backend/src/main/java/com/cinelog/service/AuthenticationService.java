package com.cinelog.service;

import com.cinelog.dto.AuthResponse;
import com.cinelog.dto.LoginRequest;
import com.cinelog.dto.RefreshTokenRequest;
import com.cinelog.dto.RegisterRequest;
import com.cinelog.dto.UserDto;
import com.cinelog.entity.RefreshToken;
import com.cinelog.entity.User;
import com.cinelog.exception.InvalidCredentialsException;
import com.cinelog.exception.InvalidRefreshTokenException;
import com.cinelog.repository.RefreshTokenRepository;
import com.cinelog.repository.UserRepository;
import com.cinelog.security.CustomUserPrincipal;
import com.cinelog.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(UserService userService, UserRepository userRepository,
                                  RefreshTokenRepository refreshTokenRepository,
                                  AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        return userService.registerUser(request);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException ex) {
            log.warn("Failed login attempt, username={}", request.username());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        AuthResponse response = issueTokens(user);
        log.info("Successful login, userId={}", user.getId());
        return response;
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(storedToken.getToken());
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        User user = storedToken.getUser();
        refreshTokenRepository.deleteByToken(storedToken.getToken());
        AuthResponse response = issueTokens(user);
        log.info("Refreshed tokens, userId={}", user.getId());
        return response;
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.deleteByToken(request.refreshToken());
        log.info("Logged out, refresh token invalidated");
    }

    private AuthResponse issueTokens(User user) {
        CustomUserPrincipal principal = CustomUserPrincipal.fromUser(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtService.getRefreshTokenExpirationMs()))
                .build();
        refreshTokenRepository.save(tokenEntity);

        return new AuthResponse(accessToken, refreshToken);
    }
}
