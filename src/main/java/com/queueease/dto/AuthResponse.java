package com.queueease.dto;

import com.queueease.entity.Role;

public class AuthResponse {
    private String token;
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Long businessId;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String name, String email, Role role, Long businessId) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.businessId = businessId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }
}
