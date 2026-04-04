package com.balaji.ledgerguard.exception;

public class FinancialRecordNotFoundException extends ResourceNotFoundException {

    public FinancialRecordNotFoundException(Long recordId) {
        super("Financial record not found with id: " + recordId);
    }
}
