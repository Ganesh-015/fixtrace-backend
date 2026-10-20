package com.fixtrace.controller;

import com.fixtrace.dto.ApiResponse;
import com.fixtrace.dto.PageResponse;
import com.fixtrace.dto.user.UpdateUserRequest;
import com.fixtrace.dto.user.UserResponse;
import com.fixtrace.enums.Role;
import com.fixtrace.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing system users, technicians, and account status")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List users with role filtering and pagination", description = "Admin only. Retrieves paginated list of users.")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort
    ) {
        int safeSize = Math.min(Math.max(size, 1), 100);

        Sort.Direction direction = Sort.Direction.ASC;
        String sortField = "id";
        if (sort.length > 0) {
            String[] parts = sort[0].split(",");
            sortField = parts[0];
            if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(direction, sortField));
        PageResponse<UserResponse> response = userService.getAllUsers(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/technicians")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active technicians", description = "Admin only. Returns all active technicians for assignment dropdowns.")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getActiveTechnicians() {
        List<UserResponse> response = userService.getActiveTechnicians();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Admin or user themselves can view user profile.")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserResponse response = userService.getUserById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile", description = "Admin can update all fields including role/active status; users can edit own details.")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserResponse response = userService.updateUser(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user", description = "Admin only. Deactivates user account preventing future logins.")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
    }
}
