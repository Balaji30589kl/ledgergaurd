package com.balaji.ledgerguard.dto.request;

import com.balaji.ledgerguard.enums.RoleType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Email(message = "Email must be valid")
    @Size(max = 120, message = "Email must be at most 120 characters")
    private String email;

    private RoleType role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
}
