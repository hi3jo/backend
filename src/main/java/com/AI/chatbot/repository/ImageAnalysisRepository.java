package com.AI.chatbot.repository;

import com.AI.chatbot.model.ImageAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;  // 올바른 Optional 임포트

@Repository
public interface ImageAnalysisRepository extends JpaRepository<ImageAnalysis, Long> {
    List<ImageAnalysis> findByUserId(Long userId);

    Optional<ImageAnalysis> findByFileHash(String fileHash);
}
