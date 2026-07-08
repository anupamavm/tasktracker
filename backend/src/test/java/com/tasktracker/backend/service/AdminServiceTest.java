package com.tasktracker.backend.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private SuperAdminService adminService;

	private SecurityContext originalContext;

	@BeforeEach
	void setUp() {
		originalContext = SecurityContextHolder.getContext();
		SecurityContext ctx = mock(SecurityContext.class);
		Authentication auth = mock(Authentication.class);
		when(ctx.getAuthentication()).thenReturn(auth);
		when(ctx.getAuthentication().getName()).thenReturn("currentAdmin");
		SecurityContextHolder.setContext(ctx);
		ReflectionTestUtils.setField(adminService, "seededSuperAdminUsername", "currentAdmin");
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.setContext(originalContext);
	}

	@Test
	void changeUserRole_success_whenSeededSuperAdminChangesRegularUser() {
		User target = User.builder().id(1L).username("regularUser").role(Role.USER).build();
		when(userRepository.findByUsername("regularUser")).thenReturn(Optional.of(target));

		adminService.changeUserRole("regularUser", "admin");

		assertThat(target.getRole()).isEqualTo(Role.ADMIN);
	}

	@Test
	void changeUserRole_invalidRole_throws() {
		User target = User.builder().id(2L).username("regularUser").role(Role.USER).build();
		when(userRepository.findByUsername("regularUser")).thenReturn(Optional.of(target));

		assertThatThrownBy(() -> adminService.changeUserRole("regularUser", "nonexistent"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Invalid role tier assignment provided");
	}

	@Test
	void changeUserRole_attemptModifyOtherAdmin_throwsSecurityException() {
		User target = User.builder().id(3L).username("otherAdmin").role(Role.ADMIN).build();
		when(userRepository.findByUsername("otherAdmin")).thenReturn(Optional.of(target));

		assertThatThrownBy(() -> adminService.changeUserRole("otherAdmin", "user"))
				.isInstanceOf(SecurityException.class)
				.hasMessageContaining("Access Denied");
	}
}