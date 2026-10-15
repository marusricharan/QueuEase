package com.queueease.controller;

import com.queueease.dto.BookingDTO;
import com.queueease.dto.CreateBookingRequest;
import com.queueease.dto.UpdateBookingStatusRequest;
import com.queueease.entity.User;
import com.queueease.service.AuthService;
import com.queueease.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    public BookingController(BookingService bookingService, AuthService authService) {
        this.bookingService = bookingService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        try {
            User customer = authService.getCurrentAuthenticatedUser();
            BookingDTO dto = bookingService.createBooking(customer, request);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings() {
        try {
            User customer = authService.getCurrentAuthenticatedUser();
            List<BookingDTO> list = bookingService.getCustomerBookings(customer);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BookingDTO>> getBusinessBookings(@PathVariable Long businessId) {
        return ResponseEntity.ok(bookingService.getBusinessBookings(businessId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateBookingStatusRequest request) {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            BookingDTO dto = bookingService.updateStatus(id, request, user);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            User customer = authService.getCurrentAuthenticatedUser();
            bookingService.cancelBooking(id, customer);
            return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
