package com.queueease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(nullable = false)
    private String customerName;

    private String customerPhone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(nullable = false)
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status = QueueStatus.WAITING;

    private Integer counterNumber;

    @Column(nullable = false)
    private LocalDateTime joinedTime = LocalDateTime.now();

    private LocalDateTime calledTime;

    private LocalDateTime servedTime;

    private LocalDateTime completedTime;

    private Integer estimatedWaitMinutes = 0;

    private String notes;

    public QueueEntry() {}

    public QueueEntry(Business business, User customer, String customerName, String customerPhone,
                      ServiceEntity service, String ticketNumber) {
        this.business = business;
        this.customer = customer;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.service = service;
        this.ticketNumber = ticketNumber;
        this.status = QueueStatus.WAITING;
        this.joinedTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Business getBusiness() { return business; }
    public void setBusiness(Business business) { this.business = business; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
