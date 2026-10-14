package com.queueease.dto;

import com.queueease.entity.QueueStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateQueueStatusRequest {
    @NotNull(message = "Status is required")
    private QueueStatus status;

    private Integer counterNumber;
    private String notes;

    public UpdateQueueStatusRequest() {}

    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }

    public Integer getCounterNumber() { return counterNumber; }
    public void setCounterNumber(Integer counterNumber) { this.counterNumber = counterNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
