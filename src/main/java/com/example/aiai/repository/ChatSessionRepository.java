package com.example.aiai.repository;

import com.example.aiai.domain.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
}
