package com.AI.chatbot.controller;

import com.AI.chatbot.dto.ReviewDTO;
import com.AI.chatbot.model.Review;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.ReviewRepository;
import com.AI.chatbot.repository.UserRepository;
import com.AI.chatbot.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping
    public ResponseEntity<ReviewDTO> saveReview(@RequestBody ReviewDTO reviewDTO) {
        if (reviewDTO.getLawyerId() == null) {
            throw new IllegalArgumentException("Lawyer ID is required");
        }
        if (reviewDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User lawyer = userRepository.findById(reviewDTO.getLawyerId())
                .orElseThrow(() -> new IllegalArgumentException("Lawyer not found"));
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Review review = new Review();
        review.setLawyer(lawyer);
        review.setUser(user);
        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());

        Review savedReview = reviewRepository.save(review);

        reviewDTO.setId(savedReview.getId());
        reviewDTO.setUserNickname(user.getNickname());

        return ResponseEntity.ok(reviewDTO);
    }

    @GetMapping("/lawyer/{lawyerId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByLawyerId(@PathVariable("lawyerId") Long lawyerId) {
        User lawyer = new User();
        lawyer.setId(lawyerId);
        List<Review> reviews = reviewService.getReviewsByLawyer(lawyer);
        List<ReviewDTO> reviewDTOs = reviews.stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setId(review.getId());
            dto.setLawyerId(review.getLawyer().getId());
            dto.setUserId(review.getUser().getId());
            dto.setUserNickname(review.getUser().getNickname());
            dto.setComment(review.getComment());
            dto.setRating(review.getRating());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    @GetMapping("/lawyer/{lawyerId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable("lawyerId") Long lawyerId) {
        User lawyer = new User();
        lawyer.setId(lawyerId);
        double averageRating = reviewService.getAverageRating(lawyer);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return ResponseEntity.ok(reviews);
    }
}
