package com.balaji.ledgerguard.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.balaji.ledgerguard.enums.RecordType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public class UpdateFinancialRecordRequest {

    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    private RecordType type;

    @Size(max = 100, message = "Category must be at most 100 characters")
    private String category;

    private LocalDate date;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    private Long userId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
