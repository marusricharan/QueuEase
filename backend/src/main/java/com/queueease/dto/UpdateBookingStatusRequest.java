package com.queueease.dto;

import com.queueease.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateBookingStatusRequest {
    @NotNull(message = "Status is required")
    private BookingStatus status;

    private String notes;

    public UpdateBookingStatusRequest() {}

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
