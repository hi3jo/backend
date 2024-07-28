package com.AI.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.AI.chatbot.model.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

}
