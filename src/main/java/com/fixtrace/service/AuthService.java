package com.fixtrace.service;

import com.fixtrace.dto.auth.LoginRequest;
import com.fixtrace.dto.auth.LoginResponse;
import com.fixtrace.dto.auth.RegisterRequest;
import com.fixtrace.dto.user.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse getCurrentUser(String email);
}
