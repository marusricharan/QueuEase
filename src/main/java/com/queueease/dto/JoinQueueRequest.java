package com.queueease.dto;

import jakarta.validation.constraints.NotNull;

public class JoinQueueRequest {
    @NotNull(message = "Business ID is required")
    private Long businessId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private String customerName;
    private String customerPhone;
    private String notes;

    public JoinQueueRequest() {}

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
