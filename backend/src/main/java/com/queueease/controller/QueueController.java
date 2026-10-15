package com.queueease.controller;

import com.queueease.dto.JoinQueueRequest;
import com.queueease.dto.QueueEntryDTO;
import com.queueease.entity.User;
import com.queueease.service.AuthService;
import com.queueease.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;
    private final AuthService authService;

    public QueueController(QueueService queueService, AuthService authService) {
        this.queueService = queueService;
        this.authService = authService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(@Valid @RequestBody JoinQueueRequest request) {
        try {
            User customer = authService.getCurrentAuthenticatedUser();
            QueueEntryDTO entry = queueService.joinQueue(customer, request);
            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leaveQueue(@PathVariable Long id) {
        try {
            User customer = authService.getCurrentAuthenticatedUser();
            queueService.leaveQueue(id, customer);
            return ResponseEntity.ok(Map.of("message", "Successfully left the queue"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/business/{businessId}/active")
    public ResponseEntity<?> getActiveQueueEntry(@PathVariable Long businessId) {
        try {
            User customer = authService.getCurrentAuthenticatedUser();
            Optional<QueueEntryDTO> entry = queueService.getCustomerActiveEntry(businessId, customer);
            return entry.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.ok(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-history")
    public ResponseEntity<?> getMyQueueHistory() {
        try {
            User customer = authService.getCurrentAuthenticatedUser();
            List<QueueEntryDTO> history = queueService.getCustomerQueueHistory(customer);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/business/{businessId}/public")
    public ResponseEntity<?> getPublicQueue(@PathVariable Long businessId) {
        try {
            List<QueueEntryDTO> list = queueService.getBusinessQueue(businessId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
