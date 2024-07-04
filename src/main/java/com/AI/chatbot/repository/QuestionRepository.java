package com.AI.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.AI.chatbot.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Question q SET q.answer = :answer WHERE q.id = :id")
    Integer updateAnswer(@Param("id") Long id, @Param("answer") String answer);
}