package com.fixtrace.service.impl;

import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.user.UpdateUserRequest;
import com.fixtrace.dto.user.UserResponse;
import com.fixtrace.entity.User;
import com.fixtrace.enums.Role;
import com.fixtrace.exception.ForbiddenException;
import com.fixtrace.exception.ResourceNotFoundException;
import com.fixtrace.mapper.UserMapper;
import com.fixtrace.repository.UserRepository;
import com.fixtrace.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Role role, Pageable pageable) {
        Page<User> page;
        if (role != null) {
            page = userRepository.findByRole(role, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        List<UserResponse> content = page.getContent().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Only Admin or the user themselves can view full user details
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {
            throw new ForbiddenException("You do not have permission to view this user profile.");
        }

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, String currentUserEmail) {
        log.info("Updating user with ID {} requested by {}", id, currentUserEmail);

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        // Only Admin or the user themselves can edit
        if (!isAdmin && !currentUser.getId().equals(id)) {
            throw new ForbiddenException("You cannot edit another user's profile.");
        }

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        // Only Admin can change role or active status
        if (isAdmin) {
            if (request.getRole() != null) {
                user.setRole(request.getRole());
            }
            if (request.getActive() != null) {
                user.setActive(request.getActive());
            }
        }

        User saved = userRepository.save(user);
        log.info("User {} updated successfully", saved.getEmail());

        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setActive(false);
        userRepository.save(user);
        log.info("User {} deactivated successfully", user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveTechnicians() {
        return userRepository.findByRoleAndActiveTrue(Role.TECHNICIAN).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
