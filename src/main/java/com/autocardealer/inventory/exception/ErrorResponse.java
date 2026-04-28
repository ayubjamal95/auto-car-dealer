package com.autocardealer.inventory.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response structure returned by the API.
 * Provides consistent error information across all endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Error message
     */
    private String message;

    /**
     * Request path where the error occurred
     */
    private String path;

    /**
     * List of validation errors (for 400 responses)
     */
    private List<ValidationError> errors;

    /**
     * Represents a single field validation error
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
    }
}