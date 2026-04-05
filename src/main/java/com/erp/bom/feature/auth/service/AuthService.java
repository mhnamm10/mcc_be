package com.erp.bom.feature.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.bom.feature.auth.dto.LoginRequest;
import com.erp.bom.feature.auth.dto.LoginResponse;
import com.erp.bom.feature.auth.dto.UserResponse;
import com.erp.bom.feature.auth.entity.User;
import com.erp.bom.feature.auth.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserMapper userMapper, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        log.info("[LOGIN] Attempting login for username: {}", request.getUsername());
        
        // Find user by username
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(wrapper);

        log.info("[LOGIN] User found: {}, password in DB: {} chars", 
            user != null ? user.getUsername() : "NULL",
            user != null ? user.getPassword().length() : 0);

        // Validate credentials
        if (user == null) {
            log.warn("[LOGIN] User not found: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }
        
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        log.info("[LOGIN] Password match result: {}", passwordMatches);
        
        if (!passwordMatches) {
            log.warn("[LOGIN] Password mismatch for user: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Generate token
        String token = jwtService.generateToken(user.getUsername(), user.getId(), user.getRole());
        log.info("[LOGIN] Token generated successfully for: {}", user.getUsername());

        // Build response
        UserResponse userResponse = new UserResponse(
                user.getId().toString(),
                user.getName(),
                user.getUsername()
        );

        return new LoginResponse(token, userResponse);
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }
}
