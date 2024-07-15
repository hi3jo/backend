package com.AI.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.AI.chatbot.model.ImageAnalysis;

@Repository
public interface ImageAnalysisRepository extends JpaRepository<ImageAnalysis, Long>{
    
}
