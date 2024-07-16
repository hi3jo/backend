package com.AI.chatbot.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.AI.chatbot.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByTitleContaining(String title, Pageable pageable);

  Page<Post> findByContentContaining(String content, Pageable pageable);

  @Modifying
  @Transactional
  @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
  void incrementViewCount(@Param("postId") Long postId);

  @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
  Page<Post> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

  @Query("SELECT p FROM Post p WHERE p.likeCount >= :likeCount")
  Page<Post> findByPopularPosts(@Param("likeCount") int likeCount, Pageable pageable);

  @Query("SELECT p FROM Post p WHERE p.likeCount >= :likeCount AND (p.title LIKE CONCAT('%', :keyword, '%') OR p.content LIKE CONCAT('%', :keyword, '%'))")
  Page<Post> findByPopularPostsBySearch(@Param("likeCount") int likeCount, @Param("keyword") String keyword, Pageable pageable);

  @Query("SELECT p FROM Post p ORDER BY p.likeCount DESC")
  List<Post> findTopPosts(Pageable pageable);
}
