package com.cinelog.controller;

import com.cinelog.dto.AuthResponse;
import com.cinelog.dto.LoginRequest;
import com.cinelog.dto.RefreshTokenRequest;
import com.cinelog.dto.RegisterRequest;
import com.cinelog.dto.UserDto;
import com.cinelog.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        UserDto dto = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authenticationService.refresh(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
