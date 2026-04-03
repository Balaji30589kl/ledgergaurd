package com.balaji.ledgerguard.service;

import java.time.LocalDate;
import java.util.List;

import com.balaji.ledgerguard.dto.request.CreateFinancialRecordRequest;
import com.balaji.ledgerguard.dto.request.UpdateFinancialRecordRequest;
import com.balaji.ledgerguard.dto.response.FinancialRecordResponse;
import com.balaji.ledgerguard.enums.RecordType;

public interface FinancialRecordService {

    FinancialRecordResponse createRecord(CreateFinancialRecordRequest request);

    List<FinancialRecordResponse> getRecords(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Long userId
    );

    FinancialRecordResponse getRecordById(Long id);

    FinancialRecordResponse updateRecord(Long id, UpdateFinancialRecordRequest request);

    void deleteRecord(Long id);
}
