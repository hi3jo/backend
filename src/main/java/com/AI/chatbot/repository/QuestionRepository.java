package com.AI.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AI.chatbot.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

}