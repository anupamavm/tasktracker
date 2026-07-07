package com.tasktracker.backend.service;

import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public void changeUserRole(String username, String newRoleStr) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Target user context not found"));
        try {
            Role targetRole = Role.valueOf(newRoleStr.toUpperCase());
            user.setRole(targetRole);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role provided");
        }
    }
}