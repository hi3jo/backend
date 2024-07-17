package com.AI.chatbot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.AI.chatbot.model.ChatBot;

public interface ChatbotRepository extends JpaRepository<ChatBot, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE ChatBot q SET q.answer = :answer WHERE q.id = :id")
    Integer updateAnswer(@Param("id") Long id, @Param("answer") String answer);

    List<ChatBot> findByHistoryId(Long historyId);

    // 기존 질문과 히스토리 ID를 기반으로 ChatBot 엔티티 검색
    Optional<ChatBot> findByAskAndHistoryId(String ask, Long historyId);
}