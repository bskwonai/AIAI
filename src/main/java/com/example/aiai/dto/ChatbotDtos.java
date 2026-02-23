package com.example.aiai.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class ChatbotDtos {
    public record CreateChatbotRequest(@NotBlank String name, @NotBlank String model, @NotBlank String systemPrompt) {}
    public record ChatbotResponse(Long id, Long workspaceId, String name, String model, Instant createdAt) {}
}
