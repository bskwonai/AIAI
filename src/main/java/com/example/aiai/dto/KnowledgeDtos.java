package com.example.aiai.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class KnowledgeDtos {
    public record CreateDocumentRequest(@NotBlank String title, @NotBlank String content) {}
    public record DocumentResponse(Long id, Long workspaceId, String title, Instant createdAt) {}
}
