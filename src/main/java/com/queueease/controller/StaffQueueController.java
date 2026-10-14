package com.queueease.controller;

import com.queueease.dto.DailySummaryDTO;
import com.queueease.dto.JoinQueueRequest;
import com.queueease.dto.QueueEntryDTO;
import com.queueease.dto.UpdateQueueStatusRequest;
import com.queueease.entity.User;
import com.queueease.service.AnalyticsService;
import com.queueease.service.AuthService;
import com.queueease.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
public class StaffQueueController {

    private final QueueService queueService;
    private final AnalyticsService analyticsService;
    private final AuthService authService;

    public StaffQueueController(QueueService queueService,
                                AnalyticsService analyticsService,
                                AuthService authService) {
        this.queueService = queueService;
        this.analyticsService = analyticsService;
        this.authService = authService;
    }

    @GetMapping("/queue/business/{businessId}")
    public ResponseEntity<List<QueueEntryDTO>> getLiveQueue(@PathVariable Long businessId) {
        return ResponseEntity.ok(queueService.getBusinessQueue(businessId));
    }

    @GetMapping("/queue/business/{businessId}/completed")
    public ResponseEntity<List<QueueEntryDTO>> getCompletedToday(@PathVariable Long businessId) {
        return ResponseEntity.ok(queueService.getBusinessCompletedToday(businessId));
    }

    @PostMapping("/queue/business/{businessId}/call-next")
    public ResponseEntity<?> callNextCustomer(@PathVariable Long businessId,
                                              @RequestParam(required = false, defaultValue = "1") Integer counterNumber) {
        try {
            User staff = authService.getCurrentAuthenticatedUser();
            QueueEntryDTO entry = queueService.callNext(businessId, counterNumber, staff);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/queue/{id}/status")
    public ResponseEntity<?> updateQueueStatus(@PathVariable Long id,
                                               @Valid @RequestBody UpdateQueueStatusRequest request) {
        try {
            User staff = authService.getCurrentAuthenticatedUser();
            QueueEntryDTO entry = queueService.updateStatus(id, request, staff);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/queue/business/{businessId}/walk-in")
    public ResponseEntity<?> addWalkInCustomer(@PathVariable Long businessId,
                                               @Valid @RequestBody JoinQueueRequest request) {
        try {
            request.setBusinessId(businessId);
            QueueEntryDTO entry = queueService.joinQueue(null, request);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/business/{businessId}/analytics")
    public ResponseEntity<DailySummaryDTO> getBusinessAnalytics(
            @PathVariable Long businessId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(analyticsService.getDailySummary(businessId, date));
    }
}
