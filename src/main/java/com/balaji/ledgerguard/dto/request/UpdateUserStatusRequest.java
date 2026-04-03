package com.balaji.ledgerguard.dto.request;

import com.balaji.ledgerguard.enums.UserStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateUserStatusRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
