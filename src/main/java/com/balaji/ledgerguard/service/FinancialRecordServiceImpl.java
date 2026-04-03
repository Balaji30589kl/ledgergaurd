package com.balaji.ledgerguard.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.balaji.ledgerguard.dto.request.CreateFinancialRecordRequest;
import com.balaji.ledgerguard.dto.request.UpdateFinancialRecordRequest;
import com.balaji.ledgerguard.dto.response.FinancialRecordResponse;
import com.balaji.ledgerguard.entity.FinancialRecord;
import com.balaji.ledgerguard.entity.User;
import com.balaji.ledgerguard.enums.RecordType;
import com.balaji.ledgerguard.exception.FinancialRecordNotFoundException;
import com.balaji.ledgerguard.exception.InvalidOperationException;
import com.balaji.ledgerguard.exception.UserNotFoundException;
import com.balaji.ledgerguard.repository.FinancialRecordRepository;
import com.balaji.ledgerguard.repository.UserRepository;

@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    public FinancialRecordServiceImpl(FinancialRecordRepository financialRecordRepository, UserRepository userRepository) {
        this.financialRecordRepository = financialRecordRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public FinancialRecordResponse createRecord(CreateFinancialRecordRequest request) {
        User user = findUserById(request.getUserId());

        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(normalizeCategory(request.getCategory()));
        record.setDate(request.getDate());
        record.setNotes(normalizeNotes(request.getNotes()));
        record.setUser(user);

        FinancialRecord savedRecord = financialRecordRepository.save(record);
        return toResponse(savedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecords(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Long userId
    ) {
        validateDateRange(startDate, endDate);

        Specification<FinancialRecord> specification = (root, query, cb) -> cb.conjunction();

        if (type != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        if (category != null && !category.trim().isEmpty()) {
            String normalizedCategory = category.trim().toLowerCase();
            specification = specification.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), normalizedCategory));
        }

        if (startDate != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), startDate));
        }

        if (endDate != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), endDate));
        }

        if (userId != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }

        return financialRecordRepository.findAll(specification)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecordResponse getRecordById(Long id) {
        FinancialRecord record = findRecordById(id);
        return toResponse(record);
    }

    @Override
    @Transactional
    public FinancialRecordResponse updateRecord(Long id, UpdateFinancialRecordRequest request) {
        FinancialRecord record = findRecordById(id);

        boolean hasAnyFieldToUpdate = false;

        if (request.getAmount() != null) {
            record.setAmount(request.getAmount());
            hasAnyFieldToUpdate = true;
        }

        if (request.getType() != null) {
            record.setType(request.getType());
            hasAnyFieldToUpdate = true;
        }

        if (request.getCategory() != null) {
            record.setCategory(normalizeCategory(request.getCategory()));
            hasAnyFieldToUpdate = true;
        }

        if (request.getDate() != null) {
            record.setDate(request.getDate());
            hasAnyFieldToUpdate = true;
        }

        if (request.getNotes() != null) {
            record.setNotes(normalizeNotes(request.getNotes()));
            hasAnyFieldToUpdate = true;
        }

        if (request.getUserId() != null) {
            User user = findUserById(request.getUserId());
            record.setUser(user);
            hasAnyFieldToUpdate = true;
        }

        if (!hasAnyFieldToUpdate) {
            throw new InvalidOperationException("At least one field must be provided for record update");
        }

        FinancialRecord updatedRecord = financialRecordRepository.save(record);
        return toResponse(updatedRecord);
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        FinancialRecord record = findRecordById(id);
        financialRecordRepository.delete(record);
    }

    private FinancialRecord findRecordById(Long id) {
        return financialRecordRepository.findById(id)
                .orElseThrow(() -> new FinancialRecordNotFoundException(id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidOperationException("startDate must be before or equal to endDate");
        }
    }

    private String normalizeCategory(String category) {
        String normalized = category == null ? null : category.trim();
        if (normalized == null || normalized.isEmpty()) {
            throw new InvalidOperationException("Category must not be blank");
        }
        return normalized;
    }

    private String normalizeNotes(String notes) {
        if (notes == null) {
            return null;
        }
        String normalized = notes.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private FinancialRecordResponse toResponse(FinancialRecord record) {
        FinancialRecordResponse response = new FinancialRecordResponse();
        response.setId(record.getId());
        response.setAmount(record.getAmount());
        response.setType(record.getType());
        response.setCategory(record.getCategory());
        response.setDate(record.getDate());
        response.setNotes(record.getNotes());
        response.setUserId(record.getUser().getId());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        return response;
    }
}
