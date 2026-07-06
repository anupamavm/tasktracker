package com.tasktracker.backend.service;

import com.tasktracker.backend.dto.TaskRequest;
import com.tasktracker.backend.dto.TaskResponse;
import com.tasktracker.backend.exception.ResourceNotFoundException;
import com.tasktracker.backend.model.Role;
import com.tasktracker.backend.model.Task;
import com.tasktracker.backend.model.TaskStatus;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public TaskResponse createTask(TaskRequest request, User currentUser) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .dueDate(request.getDueDate())
                .owner(currentUser)
                .build();

        Task savedTask = taskRepository.save(task);
        TaskResponse response = mapToResponse(savedTask);

        messagingTemplate.convertAndSend("/topic/tasks", response);
        return response;
    }

    public Page<TaskResponse> getTasks(User currentUser, TaskStatus status, Long ownerId, Pageable pageable) {
        if (currentUser.getRole() == Role.ADMIN) {
            return taskRepository.findByFilters(ownerId, status, pageable).map(this::mapToResponse);
        } else {
            return taskRepository.findByFilters(currentUser.getId(), status, pageable).map(this::mapToResponse);
        }
    }

    public TaskResponse getTaskById(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (currentUser.getRole() != Role.ADMIN && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view this task");
        }

        return mapToResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (currentUser.getRole() != Role.ADMIN && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        TaskResponse response = mapToResponse(updatedTask);

        messagingTemplate.convertAndSend("/topic/tasks", response);
        return response;
    }

    public void deleteTask(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (currentUser.getRole() != Role.ADMIN && !task.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this task");
        }

        taskRepository.delete(task);
        messagingTemplate.convertAndSend("/topic/tasks", "Deleted Task ID: " + id);
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .ownerId(task.getOwner().getId())
                .ownerUsername(task.getOwner().getUsername())
                .build();
    }
}