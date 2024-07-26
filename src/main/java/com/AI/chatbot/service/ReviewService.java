package com.AI.chatbot.service;

import com.AI.chatbot.model.Review;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByLawyer(User lawyer) {
        return reviewRepository.findByLawyer(lawyer);
    }

    public double getAverageRating(User lawyer) {
        List<Review> reviews = reviewRepository.findByLawyer(lawyer);
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
}
