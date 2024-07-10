package com.AI.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.AI.chatbot.model.History;
import com.AI.chatbot.model.User;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserId(Long userId);

    Optional<History> findTopByUserOrderByLastChatBotDateDesc(User user);
}
