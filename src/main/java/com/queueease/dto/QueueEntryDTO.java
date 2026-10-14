package com.queueease.dto;

import com.queueease.entity.QueueStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QueueEntryDTO {
    private Long id;
    private Long businessId;
    private String businessName;
    private String businessCategory;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long serviceId;
    private String serviceName;
    private Integer serviceDuration;
    private BigDecimal servicePrice;
    private String ticketNumber;
    private QueueStatus status;
    private Integer counterNumber;
    private LocalDateTime joinedTime;
    private LocalDateTime calledTime;
    private LocalDateTime servedTime;
    private LocalDateTime completedTime;
    private Integer estimatedWaitMinutes;
    private long peopleAhead;
    private String notes;

    public QueueEntryDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getBusinessCategory() { return businessCategory; }
    public void setBusinessCategory(String businessCategory) { this.businessCategory = businessCategory; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public Integer getServiceDuration() { return serviceDuration; }
    public void setServiceDuration(Integer serviceDuration) { this.serviceDuration = serviceDuration; }

    public BigDecimal getServicePrice() { return servicePrice; }
    public void setServicePrice(BigDecimal servicePrice) { this.servicePrice = servicePrice; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }

    public Integer getCounterNumber() { return counterNumber; }
    public void setCounterNumber(Integer counterNumber) { this.counterNumber = counterNumber; }

    public LocalDateTime getJoinedTime() { return joinedTime; }
    public void setJoinedTime(LocalDateTime joinedTime) { this.joinedTime = joinedTime; }

    public LocalDateTime getCalledTime() { return calledTime; }
    public void setCalledTime(LocalDateTime calledTime) { this.calledTime = calledTime; }

    public LocalDateTime getServedTime() { return servedTime; }
    public void setServedTime(LocalDateTime servedTime) { this.servedTime = servedTime; }

    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }

    public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; }

    public long getPeopleAhead() { return peopleAhead; }
    public void setPeopleAhead(long peopleAhead) { this.peopleAhead = peopleAhead; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
