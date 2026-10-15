package com.queueease.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private Long businessId;
    private String customerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewDTO() {}

    public ReviewDTO(Long id, Long businessId, String customerName, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.businessId = businessId;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
