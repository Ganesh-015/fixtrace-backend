package com.fixtrace.service.impl;

import com.fixtrace.dto.auth.LoginRequest;
import com.fixtrace.dto.auth.LoginResponse;
import com.fixtrace.dto.auth.RegisterRequest;
import com.fixtrace.dto.user.UserResponse;
import com.fixtrace.entity.User;
import com.fixtrace.enums.Role;
import com.fixtrace.exception.BadRequestException;
import com.fixtrace.exception.DuplicateResourceException;
import com.fixtrace.exception.ResourceNotFoundException;
import com.fixtrace.exception.UnauthorizedException;
import com.fixtrace.mapper.UserMapper;
import com.fixtrace.repository.UserRepository;
import com.fixtrace.security.JwtService;
import com.fixtrace.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.STUDENT;

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(assignedRole);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}, role: {}", savedUser.getId(), savedUser.getRole());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        log.info("Attempting login for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isActive()) {
            log.warn("Login rejected for inactive user: {}", email);
            throw new UnauthorizedException("User account is disabled. Please contact administrator.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user: {}", email);
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        log.info("User logged in successfully: {}, role: {}", email, user.getRole());

        return new LoginResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return userMapper.toResponse(user);
    }
}
