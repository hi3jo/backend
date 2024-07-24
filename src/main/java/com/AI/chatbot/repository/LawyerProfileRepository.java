package com.AI.chatbot.repository;

import com.AI.chatbot.model.LawyerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LawyerProfileRepository extends JpaRepository<LawyerProfile, Long> {
    LawyerProfile findByUser_Id(Long userId);
}
