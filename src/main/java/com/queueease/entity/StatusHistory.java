package com.queueease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "status_histories")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long queueEntryId;

    private Long bookingId;

    private String oldStatus;

    @Column(nullable = false)
    private String newStatus;

    private String changedBy;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public StatusHistory() {}

    public StatusHistory(Long queueEntryId, Long bookingId, String oldStatus, String newStatus, String changedBy, String notes) {
        this.queueEntryId = queueEntryId;
        this.bookingId = bookingId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.notes = notes;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getQueueEntryId() { return queueEntryId; }
    public void setQueueEntryId(Long queueEntryId) { this.queueEntryId = queueEntryId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
