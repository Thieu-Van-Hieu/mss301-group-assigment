package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.web;

import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.ApiResponse;
import mss301.se1911.group.assignment.paymentservices.domain.exception.DuplicatePaymentException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.InsufficientBalanceException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.InvalidTopUpMethodException;

import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentTransactionNotFoundException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PayoutRecordNotFoundException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.WalletNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleWalletNotFound(WalletNotFoundException ex) {
        log.warn("Wallet not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage()));
    }

    @ExceptionHandler(PaymentTransactionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentTransactionNotFound(PaymentTransactionNotFoundException ex) {
        log.warn("Payment transaction not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientBalance(InsufficientBalanceException ex) {
        log.warn("Insufficient balance: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler(InvalidTopUpMethodException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTopUpMethod(InvalidTopUpMethodException ex) {
        log.warn("Invalid top-up method: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicatePayment(DuplicatePaymentException ex) {
        log.warn("Duplicate payment: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, ex.getMessage()));
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentProcessing(PaymentProcessingException ex) {
        log.warn("Payment processing error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler(PayoutRecordNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePayoutRecordNotFound(PayoutRecordNotFoundException ex) {
        log.warn("Payout record not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, "Access Denied: You do not have permission to access this resource"));
    }






    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "Validation failed", errors));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.warn("JSON parse error / Http message not readable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "Invalid request payload or format: " + ex.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "An unexpected error occurred: " + ex.getMessage()));
    }
}
