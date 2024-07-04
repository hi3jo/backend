package com.AI.chatbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.AI.chatbot.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}
