package mss301.se1911.group.assignment.deliveryservice.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. Bắt lỗi Validation (Khi DTO gửi lên không thỏa mãn @Valid, @NotNull, @NotBlank...)
     * HTTP Status: 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Gom tất cả các trường bị lỗi và message tương ứng
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Dữ liệu đầu vào không hợp lệ, vui lòng kiểm tra lại!",
                errors
        );

        log.warn("Validation error: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 2. Bắt lỗi Tham số đầu vào sai (Ví dụ: ID không tồn tại, sai định dạng...)
     * HTTP Status: 400 BAD REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );

        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 3. Bắt lỗi Sai quy trình State Machine (Ví dụ: Đơn đã giao nhưng lại bấm Hủy)
     * HTTP Status: 409 CONFLICT (Xung đột trạng thái)
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "State Conflict",
                ex.getMessage()
        );

        log.warn("State conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * 4. Lưới lọc cuối cùng (Fallback): Bắt tất cả các lỗi hệ thống chưa được định nghĩa
     * HTTP Status: 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        // Với lỗi 500, ta phải log kèm theo StackTrace (ex) để developer trace bug
        log.error("Lỗi hệ thống không xác định: ", ex);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Hệ thống đang gặp sự cố, vui lòng thử lại sau!" // Không trả ex.getMessage() ra ngoài để bảo mật
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}