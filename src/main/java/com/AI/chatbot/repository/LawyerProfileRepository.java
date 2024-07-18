package com.AI.chatbot.repository;

import com.AI.chatbot.model.LawyerProfile;
import com.AI.chatbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawyerProfileRepository extends JpaRepository<LawyerProfile, Long> {
    LawyerProfile findByUser(User user);
}
