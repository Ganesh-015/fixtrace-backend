package com.fixtrace.service;

import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.user.UpdateUserRequest;
import com.fixtrace.dto.user.UserResponse;
import com.fixtrace.enums.Role;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    PageResponse<UserResponse> getAllUsers(Role role, Pageable pageable);

    UserResponse getUserById(Long id, String currentUserEmail);

    UserResponse updateUser(Long id, UpdateUserRequest request, String currentUserEmail);

    void deactivateUser(Long id);

    List<UserResponse> getActiveTechnicians();
}
