package com.AI.chatbot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AI.chatbot.model.Like;
import com.AI.chatbot.model.Post;
import com.AI.chatbot.model.User;

public interface LikeRepository extends JpaRepository<Like, Long> {
  Optional<Like> findByPostAndUser(Post post, User user);
  boolean existsByPostAndUser(Post post, User user);
}
