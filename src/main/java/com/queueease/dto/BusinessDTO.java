package com.queueease.dto;

import java.time.LocalTime;
import java.util.List;

public class BusinessDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String address;
    private String city;
    private String phone;
    private String openingHours;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer avgServiceDurationMinutes;
    private Integer dailyCapacity;
    private boolean active;
    private boolean queueOpen;
    private Long ownerId;
    private String ownerName;

    // Live operational metrics
    private int currentWaitMinutes;
    private int waitingCount;
    private int inServiceCount;
    private double averageRating;
    private int reviewCount;

    private List<ServiceDTO> services;

    public BusinessDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public LocalTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }

    public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }

    public Integer getAvgServiceDurationMinutes() { return avgServiceDurationMinutes; }
    public void setAvgServiceDurationMinutes(Integer avgServiceDurationMinutes) { this.avgServiceDurationMinutes = avgServiceDurationMinutes; }

    public Integer getDailyCapacity() { return dailyCapacity; }
    public void setDailyCapacity(Integer dailyCapacity) { this.dailyCapacity = dailyCapacity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isQueueOpen() { return queueOpen; }
    public void setQueueOpen(boolean queueOpen) { this.queueOpen = queueOpen; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public int getCurrentWaitMinutes() { return currentWaitMinutes; }
    public void setCurrentWaitMinutes(int currentWaitMinutes) { this.currentWaitMinutes = currentWaitMinutes; }

    public int getWaitingCount() { return waitingCount; }
    public void setWaitingCount(int waitingCount) { this.waitingCount = waitingCount; }

    public int getInServiceCount() { return inServiceCount; }
    public void setInServiceCount(int inServiceCount) { this.inServiceCount = inServiceCount; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public List<ServiceDTO> getServices() { return services; }
    public void setServices(List<ServiceDTO> services) { this.services = services; }
}
