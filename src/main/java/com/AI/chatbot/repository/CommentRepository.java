package com.AI.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.AI.chatbot.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
