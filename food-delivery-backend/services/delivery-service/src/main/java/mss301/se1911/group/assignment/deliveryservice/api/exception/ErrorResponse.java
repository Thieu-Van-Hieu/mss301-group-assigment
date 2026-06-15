package mss301.se1911.group.assignment.deliveryservice.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn các trường có giá trị null để JSON gọn hơn
public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        Object details // Dùng để chứa danh sách lỗi cụ thể (ví dụ: validation errors)
) {
    // Constructor tiện ích để tự động sinh timestamp
    public ErrorResponse(int status, String error, String message) {
        this(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), status, error, message, null);
    }

    public ErrorResponse(int status, String error, String message, Object details) {
        this(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), status, error, message, details);
    }
}
