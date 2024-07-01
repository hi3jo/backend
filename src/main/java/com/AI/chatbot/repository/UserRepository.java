package com.AI.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.AI.chatbot.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserid(String userid);
}