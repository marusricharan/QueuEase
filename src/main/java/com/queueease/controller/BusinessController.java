package com.queueease.controller;

import com.queueease.dto.BusinessDTO;
import com.queueease.dto.ServiceDTO;
import com.queueease.entity.User;
import com.queueease.service.AuthService;
import com.queueease.service.BusinessService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    private final BusinessService businessService;
    private final AuthService authService;

    public BusinessController(BusinessService businessService, AuthService authService) {
        this.businessService = businessService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<BusinessDTO>> getAllBusinesses() {
        return ResponseEntity.ok(businessService.getAllActiveBusinesses());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BusinessDTO>> searchBusinesses(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(businessService.searchBusinesses(q));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BusinessDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(businessService.getBusinessesByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessDTO> getBusinessById(@PathVariable Long id) {
        return ResponseEntity.ok(businessService.getBusinessById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBusiness(@PathVariable Long id, @RequestBody BusinessDTO dto) {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            BusinessDTO updated = businessService.updateBusinessSettings(id, dto, user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{id}/services")
    public ResponseEntity<?> addService(@PathVariable Long id, @Valid @RequestBody ServiceDTO serviceDTO) {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            ServiceDTO created = businessService.addService(id, serviceDTO, user);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/services/{serviceId}")
    public ResponseEntity<?> updateService(@PathVariable Long serviceId, @Valid @RequestBody ServiceDTO serviceDTO) {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            ServiceDTO updated = businessService.updateService(serviceId, serviceDTO, user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Long serviceId) {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            businessService.deleteService(serviceId, user);
            return ResponseEntity.ok(java.util.Map.of("message", "Service removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
