package com.balaji.ledgerguard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FinancialRecordNotFoundException extends RuntimeException {

    public FinancialRecordNotFoundException(Long recordId) {
        super("Financial record not found with id: " + recordId);
    }
}
