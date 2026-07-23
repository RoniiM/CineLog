package com.cinelog.controller;

import com.cinelog.dto.UpdateProfileRequest;
import com.cinelog.dto.UserDto;
import com.cinelog.security.CustomUserPrincipal;
import com.cinelog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/me")
    public UserDto getCurrentUser(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return userService.getUserProfile(principal.getId());
    }

    @GetMapping("/{userId}")
    public UserDto getProfile(@PathVariable Long userId) {
        return userService.getUserProfile(userId);
    }

    @PutMapping("/profile")
    public UserDto updateProfile(@AuthenticationPrincipal CustomUserPrincipal principal,
                                  @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(principal.getId(), request);
    }
}
