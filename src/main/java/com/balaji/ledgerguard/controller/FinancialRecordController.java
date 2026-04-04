package com.balaji.ledgerguard.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.balaji.ledgerguard.dto.request.CreateFinancialRecordRequest;
import com.balaji.ledgerguard.dto.request.CreateRecordRequest;
import com.balaji.ledgerguard.dto.request.UpdateFinancialRecordRequest;
import com.balaji.ledgerguard.dto.request.UpdateRecordRequest;
import com.balaji.ledgerguard.dto.response.FinancialRecordResponse;
import com.balaji.ledgerguard.enums.RecordType;
import com.balaji.ledgerguard.exception.InvalidOperationException;
import com.balaji.ledgerguard.service.FinancialRecordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/records")
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    public FinancialRecordController(FinancialRecordService financialRecordService) {
        this.financialRecordService = financialRecordService;
    }

    @PostMapping
    public ResponseEntity<FinancialRecordResponse> createRecord(@Valid @RequestBody CreateRecordRequest request) {
        FinancialRecordResponse response = financialRecordService.createRecord(toCreateFinancialRecordRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FinancialRecordResponse>> getRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(financialRecordService.getRecords(type, category, startDate, endDate, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(financialRecordService.getRecordById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecordRequest request
    ) {
        return ResponseEntity.ok(financialRecordService.updateRecord(id, toUpdateFinancialRecordRequest(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        financialRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    private CreateFinancialRecordRequest toCreateFinancialRecordRequest(CreateRecordRequest request) {
        CreateFinancialRecordRequest mapped = new CreateFinancialRecordRequest();
        mapped.setAmount(request.getAmount());
        mapped.setType(request.getType());
        mapped.setCategory(request.getCategory());
        mapped.setDate(request.getDate());
        mapped.setNotes(request.getNote());
        mapped.setUserId(getCurrentUserId());
        return mapped;
    }

    private UpdateFinancialRecordRequest toUpdateFinancialRecordRequest(UpdateRecordRequest request) {
        UpdateFinancialRecordRequest mapped = new UpdateFinancialRecordRequest();
        mapped.setAmount(request.getAmount());
        mapped.setType(request.getType());
        mapped.setCategory(request.getCategory());
        mapped.setDate(request.getDate());
        mapped.setNotes(request.getNote());
        return mapped;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new InvalidOperationException("Authenticated user context is missing");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }

        if (principal instanceof String userIdText) {
            try {
                return Long.valueOf(userIdText);
            } catch (NumberFormatException ignored) {
                // Fall through to the domain-specific error below.
            }
        }

        throw new InvalidOperationException("Authenticated user context is invalid");
    }
}
