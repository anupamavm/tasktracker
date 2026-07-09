package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.TaskRequest;
import com.tasktracker.backend.dto.TaskResponse;
import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.Task;
import com.tasktracker.backend.model.TaskStatus;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_sendsMessageAndReturnsResponse() {
        User user = User.builder().id(10L).username("u1").role(Role.USER).build();

        TaskRequest req = new TaskRequest();
        req.setTitle("T");
        req.setDescription("D");
        req.setStatus(TaskStatus.PENDING);
        req.setDueDate(LocalDate.of(2030, 1, 1));

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(5L);
            return t;
        });

        TaskResponse resp = taskService.createTask(req, user);

        assertThat(resp.getId()).isEqualTo(5L);
        assertThat(resp.getTitle()).isEqualTo("T");
        assertThat(resp.getOwnerId()).isEqualTo(10L);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(org.mockito.Mockito.eq("/topic/tasks"), captor.capture());
    }

    @Test
    void getTaskById_accessDeniedForNonOwner() {
        User owner = User.builder().id(20L).username("owner").role(Role.USER).build();
        Task task = Task.builder().id(2L).title("X").owner(owner).status(TaskStatus.COMPLETED).build();

        User other = User.builder().id(21L).username("other").role(Role.USER).build();

        when(taskRepository.findById(2L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.getTaskById(2L, other))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You do not have permission");
    }

    @Test
    void updateTask_success_savesAndSendsMessage() {
        User owner = User.builder().id(30L).username("owner").role(Role.USER).build();
        Task existing = Task.builder().id(3L).title("Old").owner(owner).status(TaskStatus.PENDING).build();

        when(taskRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskRequest req = new TaskRequest();
        req.setTitle("New");
        req.setDescription("Desc");
        req.setStatus(TaskStatus.COMPLETED);
        req.setDueDate(LocalDate.of(2031, 2, 2));

        TaskResponse resp = taskService.updateTask(3L, req, owner);

        assertThat(resp.getTitle()).isEqualTo("New");
        verify(messagingTemplate).convertAndSend(org.mockito.Mockito.eq("/topic/tasks"), any(TaskResponse.class));
    }

    @Test
    void deleteTask_success_deletesAndSendsMessage() {
        User owner = User.builder().id(40L).username("owner").role(Role.USER).build();
        Task existing = Task.builder().id(4L).title("ToDelete").owner(owner).build();

        when(taskRepository.findById(4L)).thenReturn(Optional.of(existing));

        taskService.deleteTask(4L, owner);

        verify(messagingTemplate).convertAndSend(org.mockito.Mockito.eq("/topic/tasks"), org.mockito.Mockito.startsWith("Deleted Task ID:"));
    }

    @Test
    void getTasks_adminSeesAllOwners() {
        User admin = User.builder().id(50L).username("a").role(Role.ADMIN).build();

        Task t1 = Task.builder().id(1L).title("A").owner(admin).status(TaskStatus.PENDING).build();
        Page<Task> page = new PageImpl<>(List.of(t1));
        when(taskRepository.findByFilters(null, null, PageRequest.of(0, 10))).thenReturn(page);

        Page<TaskResponse> resp = taskService.getTasks(admin, null, null, PageRequest.of(0, 10));
        assertThat(resp.getContent()).hasSize(1);
    }
}