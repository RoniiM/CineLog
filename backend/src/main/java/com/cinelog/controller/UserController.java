package com.cinelog.controller;

import com.cinelog.dto.RegisterRequest;
import com.cinelog.dto.UpdateProfileRequest;
import com.cinelog.dto.UserDto;
import com.cinelog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        UserDto dto = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{userId}")
    public UserDto getProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

    @PutMapping("/{userId}")
    public UserDto updateProfile(@PathVariable Long userId, @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(userId, request);
    }
}
