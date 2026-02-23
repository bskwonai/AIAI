package com.example.aiai.controller;

import com.example.aiai.dto.ChatbotDtos;
import com.example.aiai.security.CurrentUser;
import com.example.aiai.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/chatbots")
public class ChatbotController {

    private final PlatformService platformService;
    private final CurrentUser currentUser;

    public ChatbotController(PlatformService platformService, CurrentUser currentUser) {
        this.platformService = platformService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ChatbotDtos.ChatbotResponse create(@AuthenticationPrincipal Jwt jwt,
                                              @PathVariable Long workspaceId,
                                              @RequestBody @Valid ChatbotDtos.CreateChatbotRequest request) {
        return platformService.createChatbot(currentUser.userId(jwt), workspaceId, request);
    }

    @GetMapping
    public List<ChatbotDtos.ChatbotResponse> list(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable Long workspaceId) {
        return platformService.listChatbots(currentUser.userId(jwt), workspaceId);
    }
}
