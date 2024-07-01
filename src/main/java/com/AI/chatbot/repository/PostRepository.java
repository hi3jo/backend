package com.AI.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.AI.chatbot.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
