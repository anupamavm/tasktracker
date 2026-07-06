package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.AuthResponse;
import com.tasktracker.backend.dto.LoginRequest;
import com.tasktracker.backend.dto.RegisterRequest;
import com.tasktracker.backend.exception.ResourceNotFoundException;
import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;
import com.tasktracker.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // First user can be made admin manually
                .build();

        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken, user.getUsername(), user.getRole().name());
    }

    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(newRole);
        userRepository.save(user);
    }
}