package com.example.aiai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatDtos {
    public record ChatRequest(@NotNull Long chatbotId, Long sessionId, @NotBlank String message) {}
    public record ChatResponse(Long sessionId, String answer) {}
}
