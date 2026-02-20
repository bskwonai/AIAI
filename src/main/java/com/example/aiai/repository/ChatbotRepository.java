package com.example.aiai.repository;

import com.example.aiai.domain.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotRepository extends JpaRepository<Chatbot, Long> {
    List<Chatbot> findByWorkspaceId(Long workspaceId);
}
