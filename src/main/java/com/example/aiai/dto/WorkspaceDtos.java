package com.example.aiai.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class WorkspaceDtos {
    public record CreateWorkspaceRequest(@NotBlank String name) {}
    public record WorkspaceResponse(Long id, String name, Instant createdAt) {}
}
