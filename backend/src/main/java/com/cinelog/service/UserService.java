package com.cinelog.service;

import com.cinelog.dto.RegisterRequest;
import com.cinelog.dto.UpdateProfileRequest;
import com.cinelog.dto.UserDto;
import com.cinelog.entity.User;
import com.cinelog.enums.Role;
import com.cinelog.exception.DuplicateUserException;
import com.cinelog.exception.UserNotFoundException;
import com.cinelog.mapper.UserMapper;
import com.cinelog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateUserException("Username already taken: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateUserException("Email already registered: " + request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        User saved = userRepository.save(user);
        log.info("Registered new user, userId={}", saved.getId());
        return userMapper.toDto(saved);
    }

    public UserDto getUserProfile(Long userId) {
        return userMapper.toDto(findUserOrThrow(userId));
    }

    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserOrThrow(userId);
        user.setBio(request.bio());
        user.setAvatarUrl(request.avatarUrl());
        User saved = userRepository.save(user);
        log.info("Updated profile, userId={}", userId);
        return userMapper.toDto(saved);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found, id=" + userId));
    }
}
