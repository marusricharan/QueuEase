package com.queueease.controller;

import com.queueease.dto.AuthRequest;
import com.queueease.dto.AuthResponse;
import com.queueease.dto.RegisterRequest;
import com.queueease.dto.UserDTO;
import com.queueease.entity.Business;
import com.queueease.entity.Role;
import com.queueease.entity.User;
import com.queueease.repository.BusinessRepository;
import com.queueease.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final BusinessRepository businessRepository;

    public AuthController(AuthService authService, BusinessRepository businessRepository) {
        this.authService = authService;
        this.businessRepository = businessRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User user = authService.getCurrentAuthenticatedUser();
            Long businessId = null;
            if (user.getRole() == Role.ROLE_STAFF) {
                List<Business> owned = businessRepository.findByOwner(user);
                if (!owned.isEmpty()) {
                    businessId = owned.get(0).getId();
                }
            }
            UserDTO userDTO = new UserDTO(
                    user.getId(), user.getName(), user.getEmail(), user.getRole(),
                    user.getPhone(), user.isActive(), user.getCreatedAt()
            );
            return ResponseEntity.ok(java.util.Map.of(
                    "user", userDTO,
                    "businessId", businessId != null ? businessId : 0
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
