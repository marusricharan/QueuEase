package com.queueease.service;

import com.queueease.config.JwtUtil;
import com.queueease.dto.AuthRequest;
import com.queueease.dto.AuthResponse;
import com.queueease.dto.RegisterRequest;
import com.queueease.entity.Business;
import com.queueease.entity.Role;
import com.queueease.entity.User;
import com.queueease.repository.BusinessRepository;
import com.queueease.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, BusinessRepository businessRepository,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isActive()) {
            throw new IllegalStateException("Your account has been deactivated. Please contact an administrator.");
        }

        org.springframework.security.core.userdetails.UserDetails userDetails =
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());

        Long businessId = null;
        if (user.getRole() == Role.ROLE_STAFF) {
            List<Business> owned = businessRepository.findByOwner(user);
            if (!owned.isEmpty()) {
                businessId = owned.get(0).getId();
            }
        }

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole(), businessId);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.ROLE_CUSTOMER;
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role,
                request.getPhone()
        );
        user = userRepository.save(user);

        Long businessId = null;
        if (role == Role.ROLE_STAFF && request.getBusinessName() != null && !request.getBusinessName().isBlank()) {
            Business business = new Business(
                    request.getBusinessName(),
                    request.getCategory() != null ? request.getCategory() : "General Services",
                    "Welcome to " + request.getBusinessName(),
                    request.getAddress() != null ? request.getAddress() : "Main Street",
                    request.getCity() != null ? request.getCity() : "Downtown",
                    request.getPhone() != null ? request.getPhone() : "",
                    LocalTime.of(9, 0),
                    LocalTime.of(19, 0),
                    20,
                    50,
                    user
            );
            business = businessRepository.save(business);
            businessId = business.getId();
        }

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole().name()))
                );

        String token = jwtUtil.generateToken(userDetails, user.getId(), user.getRole().name());

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole(), businessId);
    }

    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new IllegalStateException("User is not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + auth.getName()));
    }
}
