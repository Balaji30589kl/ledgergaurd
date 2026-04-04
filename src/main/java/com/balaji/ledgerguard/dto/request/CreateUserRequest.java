package com.balaji.ledgerguard.dto.request;

import com.balaji.ledgerguard.enums.RoleType;
import com.balaji.ledgerguard.enums.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must be at most 100 characters")
    private String username;

    @NotBlank(message = "Valid email required")
    @Email(message = "Valid email required")
    @Size(max = 120, message = "Email must be at most 120 characters")
    private String email;

    @NotNull(message = "Role is required")
    private RoleType role;

    private UserStatus status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Backward-compatible accessor used by existing service layer.
    public String getName() {
        return username;
    }

    // Backward-compatible mutator used by existing service layer.
    public void setName(String name) {
        this.username = name;
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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
