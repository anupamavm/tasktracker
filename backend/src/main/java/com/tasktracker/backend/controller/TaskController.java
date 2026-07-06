package com.tasktracker.backend.controller;

import com.tasktracker.backend.dto.TaskRequest;
import com.tasktracker.backend.dto.TaskResponse;
import com.tasktracker.backend.model.TaskStatus;
import com.tasktracker.backend.model.User;
import com.tasktracker.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.createTask(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(taskService.getTasks(currentUser, status, ownerId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.getTaskById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.updateTask(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        taskService.deleteTask(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}