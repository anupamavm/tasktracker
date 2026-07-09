package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.AuthResponse;
import com.tasktracker.backend.dto.LoginRequest;
import com.tasktracker.backend.dto.RegisterRequest;
import com.tasktracker.backend.exception.ResourceNotFoundException;
import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;
import com.tasktracker.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
	void register_duplicateEmail_throws() {
		RegisterRequest req = new RegisterRequest();
		req.setUsername("charlie");
		req.setEmail("duplicate@example.com");
		req.setPassword("password");

		when(userRepository.existsByUsername("charlie")).thenReturn(false);
		when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

		assertThatThrownBy(() -> authService.register(req))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Email already exists");
	}

	@Test
	void login_successful() {
		LoginRequest req = new LoginRequest();
		req.setUsername("tester");
		req.setPassword("password123");

		User existingUser = User.builder()
				.id(101L)
				.username("tester")
				.email("tester@example.com")
				.role(Role.USER)
				.build();

		when(userRepository.findByUsername("tester")).thenReturn(Optional.of(existingUser));
		when(jwtService.generateToken(existingUser)).thenReturn("valid-jwt-token");

		AuthResponse resp = authService.login(req);

		assertThat(resp).isNotNull();
		assertThat(resp.getToken()).isEqualTo("valid-jwt-token");
		assertThat(resp.getUsername()).isEqualTo("tester");
		assertThat(resp.getRole()).isEqualTo(Role.USER.name());

		verify(authenticationManager).authenticate(
				new UsernamePasswordAuthenticationToken("tester", "password123")
		);
	}

	@Test
	void login_userNotFound_throws() {
		LoginRequest req = new LoginRequest();
		req.setUsername("unknown_user");
		req.setPassword("password");

		when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login(req))
				.isInstanceOf(UsernameNotFoundException.class)
				.hasMessageContaining("User not found");
	}

	@Test
	void updateUserRole_successful() {
		User targetUser = User.builder()
				.id(1L)
				.username("user_to_upgrade")
				.role(Role.USER)
				.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(targetUser));

		authService.updateUserRole(1L, Role.ADMIN);

		assertThat(targetUser.getRole()).isEqualTo(Role.ADMIN);
		verify(userRepository).save(targetUser);
	}

	@Test
	void updateUserRole_userNotFound_throws() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.updateUserRole(999L, Role.SUPERADMIN))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User not found");
	}
}