package com.example.aiai.repository;

import com.example.aiai.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByOwnerId(String ownerId);
}
