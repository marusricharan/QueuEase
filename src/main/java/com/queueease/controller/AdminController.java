package com.queueease.controller;

import com.queueease.dto.BusinessDTO;
import com.queueease.dto.UserDTO;
import com.queueease.entity.Business;
import com.queueease.entity.User;
import com.queueease.repository.BusinessRepository;
import com.queueease.repository.QueueEntryRepository;
import com.queueease.repository.UserRepository;
import com.queueease.service.BusinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final BusinessService businessService;
    private final QueueEntryRepository queueEntryRepository;

    public AdminController(UserRepository userRepository,
                           BusinessRepository businessRepository,
                           BusinessService businessService,
                           QueueEntryRepository queueEntryRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.businessService = businessService;
        this.queueEntryRepository = queueEntryRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getRole(),
                        u.getPhone(), u.isActive(), u.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/users/{id}/toggle-active")
    public ResponseEntity<?> toggleUserActive(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User status updated", "active", user.isActive()));
    }

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessDTO>> getAllBusinesses() {
        List<BusinessDTO> list = businessRepository.findAll().stream()
                .map(businessService::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/businesses/{id}/toggle-active")
    public ResponseEntity<?> toggleBusinessActive(@PathVariable Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));
        business.setActive(!business.isActive());
        businessRepository.save(business);
        return ResponseEntity.ok(Map.of("message", "Business status updated", "active", business.isActive()));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalBusinesses = businessRepository.count();
        long totalQueues = queueEntryRepository.count();

        return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "totalBusinesses", totalBusinesses,
                "totalQueuesHandled", totalQueues
        ));
    }
}
