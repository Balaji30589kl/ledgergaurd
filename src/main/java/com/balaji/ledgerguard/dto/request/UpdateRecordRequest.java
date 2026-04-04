package com.balaji.ledgerguard.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.balaji.ledgerguard.enums.RecordType;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UpdateRecordRequest {

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private RecordType type;

    @Size(max = 100, message = "Category must be at most 100 characters")
    private String category;

    @PastOrPresent(message = "Date must be in the past or present")
    private LocalDate date;

    @Size(max = 500, message = "Note must be at most 500 characters")
    private String note;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
