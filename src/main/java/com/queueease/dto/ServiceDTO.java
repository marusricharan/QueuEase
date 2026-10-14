package com.queueease.dto;

import java.math.BigDecimal;

public class ServiceDTO {
    private Long id;
    private Long businessId;
    private String name;
    private String description;
    private Integer durationMinutes;
    private BigDecimal price;
    private boolean active;

    public ServiceDTO() {}

    public ServiceDTO(Long id, Long businessId, String name, String description, Integer durationMinutes, BigDecimal price, boolean active) {
        this.id = id;
        this.businessId = businessId;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
