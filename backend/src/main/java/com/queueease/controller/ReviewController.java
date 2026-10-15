package com.queueease.controller;

import com.queueease.dto.ReviewDTO;
import com.queueease.dto.ReviewRequest;
import com.queueease.entity.User;
import com.queueease.service.AuthService;
import com.queueease.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    public ReviewController(ReviewService reviewService, AuthService authService) {
        this.reviewService = reviewService;
        this.authService = authService;
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<ReviewDTO>> getBusinessReviews(@PathVariable Long businessId) {
        return ResponseEntity.ok(reviewService.getBusinessReviews(businessId));
    }

    @PostMapping("/business/{businessId}")
    public ResponseEntity<?> addReview(@PathVariable Long businessId, @Valid @RequestBody ReviewRequest request) {
        try {
            User customer = authService.getCurrentAuthenticatedUser();
            ReviewDTO review = reviewService.addReview(customer, businessId, request);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
