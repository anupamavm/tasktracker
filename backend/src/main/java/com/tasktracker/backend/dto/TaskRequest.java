package com.tasktracker.backend.dto;

import com.tasktracker.backend.model.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    private TaskStatus status;

    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
}