package com.tasktracker.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final UserRepository userRepository;

    @Value("${app.superadmin.username:superadmin}")
    private String seededSuperAdminUsername;

    @Transactional
    public void changeUserRole(String targetUsername, String newRoleStr) {

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Target user context not found"));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityException("Authentication context is not available.");
        }

        String currentLoggedInAdmin = authentication.getName();

        if (!currentLoggedInAdmin.equals(seededSuperAdminUsername)) {
            throw new SecurityException("Only the seeded super administrator can change user roles.");
        }

        if (targetUser.getUsername().equals(currentLoggedInAdmin)) {
            throw new SecurityException("The seeded super administrator cannot change its own role.");
        }

        try {
            Role targetRole = Role.valueOf(newRoleStr.toUpperCase());
            targetUser.setRole(targetRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role tier assignment provided");
        }
    }
}