package com.AI.chatbot.repository;

import com.AI.chatbot.model.LawyerAvailableTime;
import com.AI.chatbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LawyerAvailableTimeRepository extends JpaRepository<LawyerAvailableTime, Long> {
    List<LawyerAvailableTime> findByLawyer(User lawyer);
}
