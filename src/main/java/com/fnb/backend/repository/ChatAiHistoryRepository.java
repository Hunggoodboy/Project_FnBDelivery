package com.fnb.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fnb.backend.entity.ChatAiHistory;

import java.util.List;
import java.util.UUID;

public interface ChatAiHistoryRepository extends JpaRepository<ChatAiHistory, UUID> {
    List<ChatAiHistory> findTop30ByConversationIdOrderByCreatedAtDesc(String conversationId);
}