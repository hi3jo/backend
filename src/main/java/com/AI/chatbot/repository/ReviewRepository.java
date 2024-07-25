package com.AI.chatbot.repository;

import com.AI.chatbot.model.Review;
import com.AI.chatbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByLawyer(User lawyer);
    List<Review> findByUser(User user);
}
