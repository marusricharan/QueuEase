package com.queueease.entity;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; // e.g. "Hair & Beauty", "Automotive", "Healthcare & Clinic"

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String address;

    private String city;

    private String phone;

    private String openingHours = "09:00 - 18:00";

    private LocalTime openTime = LocalTime.of(9, 0);

    private LocalTime closeTime = LocalTime.of(18, 0);

    private Integer avgServiceDurationMinutes = 20;

    private Integer dailyCapacity = 50;

    private boolean active = true;

    private boolean queueOpen = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServiceEntity> services = new ArrayList<>();

    public Business() {}

    public Business(String name, String category, String description, String address, String city, String phone,
                    LocalTime openTime, LocalTime closeTime, Integer avgServiceDurationMinutes, Integer dailyCapacity, User owner) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.openingHours = openTime.toString() + " - " + closeTime.toString();
        this.avgServiceDurationMinutes = avgServiceDurationMinutes;
        this.dailyCapacity = dailyCapacity;
        this.owner = owner;
        this.active = true;
        this.queueOpen = true;
    }

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
    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
        if (openTime != null && closeTime != null) {
            this.openingHours = openTime.toString() + " - " + closeTime.toString();
        }
    }

    public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
        if (openTime != null && closeTime != null) {
            this.openingHours = openTime.toString() + " - " + closeTime.toString();
        }
    }

    public Integer getAvgServiceDurationMinutes() { return avgServiceDurationMinutes; }
    public void setAvgServiceDurationMinutes(Integer avgServiceDurationMinutes) { this.avgServiceDurationMinutes = avgServiceDurationMinutes; }

    public Integer getDailyCapacity() { return dailyCapacity; }
    public void setDailyCapacity(Integer dailyCapacity) { this.dailyCapacity = dailyCapacity; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isQueueOpen() { return queueOpen; }
    public void setQueueOpen(boolean queueOpen) { this.queueOpen = queueOpen; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public List<ServiceEntity> getServices() { return services; }
    public void setServices(List<ServiceEntity> services) { this.services = services; }
}
