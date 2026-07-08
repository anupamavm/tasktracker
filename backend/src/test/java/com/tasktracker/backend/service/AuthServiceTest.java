package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.AuthResponse;
import com.tasktracker.backend.dto.LoginRequest;
import com.tasktracker.backend.dto.RegisterRequest;
import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;
import com.tasktracker.backend.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthService authService;

	@Test
	void register_successful() {
		RegisterRequest req = new RegisterRequest();
		req.setUsername("alice");
		req.setEmail("alice@example.com");
		req.setPassword("plain");

		when(userRepository.existsByUsername("alice")).thenReturn(false);
		when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
		when(passwordEncoder.encode("plain")).thenReturn("encoded");
		when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User u = invocation.getArgument(0);
			u.setId(42L);
			return u;
		});

		AuthResponse resp = authService.register(req);

		assertThat(resp).isNotNull();
		assertThat(resp.getToken()).isEqualTo("jwt-token");
		assertThat(resp.getUsername()).isEqualTo("alice");
		assertThat(resp.getRole()).isEqualTo(Role.USER.name());

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());
		assertThat(captor.getValue().getPassword()).isEqualTo("encoded");
	}

	@Test
	void register_duplicateUsername_throws() {
		RegisterRequest req = new RegisterRequest();
		req.setUsername("bob");
		req.setEmail("bob@example.com");
		req.setPassword("p");

		when(userRepository.existsByUsername("bob")).thenReturn(true);

		assertThatThrownBy(() -> authService.register(req))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Username already exists");
	}

	@Test
	void login_successful() {
		LoginRequest req = new LoginRequest();
		req.setUsername("chuck");
		req.setPassword("pw");

		// authenticationManager.authenticate should not throw
		doNothing().when(authenticationManager).authenticate(any());

		User user = User.builder().id(7L).username("chuck").role(Role.USER).build();
		when(userRepository.findByUsername("chuck")).thenReturn(Optional.of(user));
		when(jwtService.generateToken(user)).thenReturn("tok");

		AuthResponse resp = authService.login(req);

		assertThat(resp.getToken()).isEqualTo("tok");
		assertThat(resp.getUsername()).isEqualTo("chuck");
	}

	@Test
	void login_userNotFound_throws() {
		LoginRequest req = new LoginRequest();
		req.setUsername("dave");
		req.setPassword("pw");

		doNothing().when(authenticationManager).authenticate(any());
		when(userRepository.findByUsername("dave")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login(req))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessageContaining("User not found");
	}

	@Test
	void updateUserRole_successful() {
		User user = User.builder().id(99L).username("victim").role(Role.USER).build();
		when(userRepository.findById(99L)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		authService.updateUserRole(99L, Role.ADMIN);

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());
		assertThat(captor.getValue().getRole()).isEqualTo(Role.ADMIN);
	}
}