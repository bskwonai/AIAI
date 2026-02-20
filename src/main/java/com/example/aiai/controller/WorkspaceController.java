package com.example.aiai.controller;

import com.example.aiai.dto.WorkspaceDtos;
import com.example.aiai.security.CurrentUser;
import com.example.aiai.service.PlatformService;
import jakarta.validation.Valid;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final PlatformService platformService;
    private final CurrentUser currentUser;

    public WorkspaceController(PlatformService platformService, CurrentUser currentUser) {
        this.platformService = platformService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public WorkspaceDtos.WorkspaceResponse create(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestBody @Valid WorkspaceDtos.CreateWorkspaceRequest request) {
        return platformService.createWorkspace(currentUser.userId(jwt), request);
    }

    @GetMapping
    public List<WorkspaceDtos.WorkspaceResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return platformService.listWorkspaces(currentUser.userId(jwt));
    }
}
