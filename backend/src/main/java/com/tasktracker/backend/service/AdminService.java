package com.tasktracker.backend.service;

import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public void changeUserRole(String targetUsername, String newRoleStr) {
        
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Target user context not found"));

        String currentLoggedInAdmin = SecurityContextHolder.getContext().getAuthentication().getName();

        if (targetUser.getRole() == Role.ADMIN && !targetUser.getUsername().equals(currentLoggedInAdmin)) {
            throw new SecurityException("Access Denied: Administrators cannot modify the role profiles of other admins.");
        }

        try {
            Role targetRole = Role.valueOf(newRoleStr.toUpperCase());
            targetUser.setRole(targetRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role tier assignment provided");
        }
    }
}