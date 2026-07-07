package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.RoleUpdateRequest;

import com.tasktracker.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/role")
    public ResponseEntity<String> updateUserRole(@RequestBody RoleUpdateRequest request) {
        adminService.changeUserRole(request.getUsername(), request.getRole());
        return ResponseEntity.ok("User role updated successfully to " + request.getRole());
    }
}