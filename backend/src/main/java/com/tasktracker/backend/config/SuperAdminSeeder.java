package com.tasktracker.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SuperAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:superadmin}")
    private String adminUsername;

    @Value("${app.admin.email:superadmin@tasktracker.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin123!}")
    private String adminPassword;

    @Override
    public void run(String... args) {


        if (userRepository.existsByUsername(adminUsername) || userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User superAdminUser = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.SUPERADMIN)
                .build();

        userRepository.save(superAdminUser);
    }
}
