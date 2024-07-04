package com.AI.chatbot.repository;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
