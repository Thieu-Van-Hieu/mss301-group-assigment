package mss301.se1911.group.assignment.commonweb.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Ngoại lệ nghiệp vụ tổng quát (Generic Business Exception) của toàn hệ thống Microservices.
 * <p>
 * Lớp này đóng vai trò là ngoại lệ đại diện duy nhất cho tất cả các lỗi nghiệp vụ có chủ đích
 * xảy ra trong hệ thống (như trùng dữ liệu, hết hạn token, sai quyền truy cập). Nó giúp triệt tiêu
 * tình trạng bùng nổ số lượng file Class Exception độc lập trong dự án.
 * </p>
 * <p>
 * <b>Cơ chế hoạt động:</b> Thay vì tạo class mới, lập trình viên chỉ cần ném ra lớp này kèm
 * theo mã trạng thái HTTP mong muốn trả về và mã lỗi nội bộ dành cho Frontend.
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @see RuntimeException
 * @since 1.0.0
 */
@Getter
public class AppException extends RuntimeException {

    /**
     * Mã trạng thái HTTP mong muốn hệ thống phản hồi về cho Client (ví dụ: 401 UNAUTHORIZED, 409 CONFLICT).
     */
    private final HttpStatus httpStatus;

    /**
     * Mã lỗi nội bộ viết hoa viết liền (Error Code) dùng để Frontend (React/Vue) bắt điều kiện logic và hiển thị đa ngôn ngữ
     * (ví dụ: "USER_ALREADY_EXISTS", "TOKEN_EXPIRED").
     */
    private final String errorCode;

    /**
     * Khởi tạo một ngoại lệ nghiệp vụ mới kèm đầy đủ thông số cấu hình mạng và định danh lỗi.
     *
     * @param httpStatus Mã trạng thái HTTP chuẩn sẽ trả về ở cửa ngõ API Gateway.
     * @param errorCode  Mã chuỗi định danh lỗi nội bộ dành riêng cho lập trình viên Frontend lập trình.
     * @param message    Tin nhắn thông báo lỗi chi tiết, thân thiện bằng ngôn ngữ đích hiển thị cho người dùng.
     */
    public AppException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
