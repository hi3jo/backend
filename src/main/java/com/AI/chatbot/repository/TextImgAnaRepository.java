package com.AI.chatbot.repository;

import com.AI.chatbot.model.TextImgAna;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TextImgAnaRepository extends JpaRepository<TextImgAna, Long> {
    List<TextImgAna> findByUser_Userid(String userId);
    Optional<TextImgAna> findByFileHash(String fileHash);
}