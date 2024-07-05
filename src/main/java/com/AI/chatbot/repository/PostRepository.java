package com.AI.chatbot.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.AI.chatbot.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByTitleContaining(String title, Pageable Pageable);

  Page<Post> findByContentContaining(String content, Pageable pageable);

  @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
  Page<Post> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);
}
