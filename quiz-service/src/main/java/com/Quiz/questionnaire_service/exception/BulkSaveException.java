package com.Quiz.questionnaire_service.exception;

public class BulkSaveException extends RuntimeException {
    public BulkSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}