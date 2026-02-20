package com.example.aiai.controller;

import com.example.aiai.dto.ChatDtos;
import com.example.aiai.security.CurrentUser;
import com.example.aiai.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final PlatformService platformService;
    private final CurrentUser currentUser;

    public ChatController(PlatformService platformService, CurrentUser currentUser) {
        this.platformService = platformService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ChatDtos.ChatResponse chat(@AuthenticationPrincipal Jwt jwt,
                                      @RequestBody @Valid ChatDtos.ChatRequest request) {
        return platformService.chat(currentUser.userId(jwt), request);
    }
}
