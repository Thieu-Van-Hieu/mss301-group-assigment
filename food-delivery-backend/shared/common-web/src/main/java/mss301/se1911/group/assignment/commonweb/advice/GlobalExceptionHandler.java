package mss301.se1911.group.assignment.commonweb.advice;

import jakarta.servlet.http.HttpServletRequest;
import mss301.se1911.group.assignment.commonweb.dto.ErrorResponse;
import mss301.se1911.group.assignment.commonweb.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Bộ xử lý ngoại lệ tập trung toàn cục (Global Exception Handler) cho toàn bộ hệ thống Microservices.
 * <p>
 * Lớp này đóng vai trò là "bệ đỡ lỗi" mặc định ở tầng ứng dụng, tự động đánh chặn và chuẩn hóa
 * các ngoại lệ phổ biến thành đối tượng phản hồi {@link ErrorResponse} trước khi gửi về phía Client.
 * </p>
 *
 * <h3>Hướng dẫn mở rộng và tùy biến tại từng Microservice con:</h3>
 * Trong thực tế, các dịch vụ nội bộ (như Identity-Service, Order-Service) hoàn toàn có thể có nhu cầu
 * bổ sung thêm các bộ bắt lỗi đặc thù hoặc định nghĩa lại (redefine) các hàm xử lý lỗi có sẵn tại đây.
 * Lập trình viên có thể thực hiện theo 2 cách sau:
 *
 * <p><b>Cách 1: Thêm bộ bắt lỗi đặc thù (Add New Handlers) - KHUYÊN DÙNG</b></p>
 * Tạo một class độc lập mới tại Microservice đó và đánh dấu bằng {@code @RestControllerAdvice}.
 * Spring Boot cho phép tồn tại nhiều cấu trúc Advice song song.
 * <pre>{@code
 * @RestControllerAdvice
 * public class IdentitySpecificExceptionHandler {
 *     @ExceptionHandler(KeycloakTimeoutException.class)
 *     public ResponseEntity<ErrorResponse> handleTimeout(KeycloakTimeoutException ex) { ... }
 * }
 * }</pre>
 *
 * <p><b>Cách 2: Định nghĩa lại / Ghi đè bộ bắt lỗi có sẵn (Redefine / Override Handlers)</b></p>
 * Tạo một class mới tại Microservice con và thực hiện kế thừa (extends) trực tiếp từ lớp cha này.
 * Khi đó, hãy dùng annotation {@code @ExceptionHandler} kết hợp với từ khóa {@code @Override} để ghi đè logic.
 * <pre>{@code
 * @RestControllerAdvice
 * @Order(Ordered.HIGHEST_PRECEDENCE) // Đẩy độ ưu tiên lên cao nhất để ép đè lớp cha ở Common
 * public class OrderLocalExceptionHandler extends GlobalExceptionHandler {
 *     @Override
 *     @ExceptionHandler(MethodArgumentNotValidException.class)
 *     public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
 *         // Logic xử lý validation đặc thù của riêng Order-Service tại đây
 *     }
 * }
 * }</pre>
 *
 * @author Thiều Văn Hiếu
 * @see RestControllerAdvice
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bộ đánh chặn đồng bộ toàn hệ thống dành riêng cho ngoại lệ nghiệp vụ chủ động (AppException).
     * <p>
     * Hàm này tự động bóc tách mã lỗi nội bộ (Internal Error Code) và trạng thái HTTP động
     * được cấu hình bên trong đối tượng ngoại lệ để xuất bản ra cấu trúc phản hồi chuẩn hóa.
     * </p>
     *
     * @param ex      Đối tượng ngoại lệ nghiệp vụ {@link AppException} chứa mã lỗi và trạng thái HTTP mong muốn.
     * @param request Đối tượng yêu cầu HTTP hiện tại phục vụ việc trích xuất đường dẫn API gây lỗi.
     * @return Một {@link ResponseEntity} chứa dữ liệu lỗi {@link ErrorResponse} kèm mã trạng thái HTTP nghiệp vụ tương ứng.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .message("[" + ex.getErrorCode() + "] " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    /**
     * Bộ đánh chặn tự động dành cho lỗi vi phạm ràng buộc dữ liệu đầu vào (Validation Error).
     * <p>
     * Hàm này kích hoạt khi các DTO đầu vào ở tầng Controller bị thất bại trong quá trình kiểm tra
     * bởi các Annotation kiểm thử (như {@code @Valid}, {@code @NotBlank}, {@code @Min}).
     * Logic hàm thực hiện gom toàn bộ danh sách các trường bị lỗi thành một chuỗi thông điệp phẳng.
     * </p>
     *
     * @param ex      Đối tượng ngoại lệ chứa danh sách chi tiết các trường bị lỗi validation đầu vào.
     * @param request Đối tượng yêu cầu HTTP hiện tại phục vụ việc trích xuất đường dẫn API gây lỗi.
     * @return Một {@link ResponseEntity} chứa dữ liệu lỗi {@link ErrorResponse} kèm mã trạng thái mặc định {@code 400 Bad Request}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
