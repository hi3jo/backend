package com.AI.chatbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AI.chatbot.model.Story;

public interface WebtoonRepository extends JpaRepository<Story, Long> {

    List<Story> findByUserId(Long userId);
}