package com.tasktracker.backend.dto;

import lombok.Data;

@Data
public class RoleUpdateRequest {
    String username;
    String role;
}
