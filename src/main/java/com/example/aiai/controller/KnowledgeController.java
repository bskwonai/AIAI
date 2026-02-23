package com.example.aiai.controller;

import com.example.aiai.dto.KnowledgeDtos;
import com.example.aiai.security.CurrentUser;
import com.example.aiai.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/documents")
public class KnowledgeController {

    private final PlatformService platformService;
    private final CurrentUser currentUser;

    public KnowledgeController(PlatformService platformService, CurrentUser currentUser) {
        this.platformService = platformService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public KnowledgeDtos.DocumentResponse create(@AuthenticationPrincipal Jwt jwt,
                                                 @PathVariable Long workspaceId,
                                                 @RequestBody @Valid KnowledgeDtos.CreateDocumentRequest request) {
        return platformService.addDocument(currentUser.userId(jwt), workspaceId, request);
    }

    @GetMapping
    public List<KnowledgeDtos.DocumentResponse> list(@AuthenticationPrincipal Jwt jwt,
                                                     @PathVariable Long workspaceId) {
        return platformService.listDocuments(currentUser.userId(jwt), workspaceId);
    }
}
