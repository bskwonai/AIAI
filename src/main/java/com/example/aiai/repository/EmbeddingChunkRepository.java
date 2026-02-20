package com.example.aiai.repository;

import com.example.aiai.domain.EmbeddingChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmbeddingChunkRepository extends JpaRepository<EmbeddingChunk, Long> {
    List<EmbeddingChunk> findByDocumentWorkspaceId(Long workspaceId);
    void deleteByDocumentId(Long documentId);
}
