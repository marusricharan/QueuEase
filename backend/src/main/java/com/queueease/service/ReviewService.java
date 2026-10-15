package com.queueease.service;

import com.queueease.dto.ReviewDTO;
import com.queueease.dto.ReviewRequest;
import com.queueease.entity.Business;
import com.queueease.entity.Review;
import com.queueease.entity.User;
import com.queueease.repository.BusinessRepository;
import com.queueease.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BusinessRepository businessRepository;

    public ReviewService(ReviewRepository reviewRepository, BusinessRepository businessRepository) {
        this.reviewRepository = reviewRepository;
        this.businessRepository = businessRepository;
    }

    @Transactional
    public ReviewDTO addReview(User customer, Long businessId, ReviewRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        Review review = new Review(business, customer, request.getRating(), request.getComment());
        review = reviewRepository.save(review);

        return mapToDTO(review);
    }

    public List<ReviewDTO> getBusinessReviews(Long businessId) {
        return reviewRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ReviewDTO mapToDTO(Review r) {
        return new ReviewDTO(
                r.getId(),
                r.getBusiness().getId(),
                r.getCustomer().getName(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
