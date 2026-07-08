package com.tasktracker.backend.controller;

import com.tasktracker.backend.service.SuperAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tasktracker.backend.dto.RoleUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PutMapping("/role")
    public ResponseEntity<String> updateUserRole(@RequestBody RoleUpdateRequest request) {
        superAdminService.changeUserRole(request.getUsername(), request.getRole());
        return ResponseEntity.ok("User role updated successfully to " + request.getRole());
    }
}