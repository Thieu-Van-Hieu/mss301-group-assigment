package mss301.se1911.group.assignment.commonweb.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Đối tượng truyền tải dữ liệu lỗi chuẩn hóa (Standardized Error Response DTO) trả về cho phía Client.
 * <p>
 * Lớp này định nghĩa cấu trúc JSON duy nhất và duy nhất cho toàn bộ hệ thống Microservices khi xảy ra lỗi.
 * Bất kể lỗi phát sinh từ phân hệ nào (Identity, Order, Product), gói tin phản hồi HTTP thất bại gửi ra thế giới
 * bên ngoài bắt buộc phải tuân theo cấu trúc phẳng này.
 * </p>
 * <p>
 * Việc duy trì cấu trúc bất biến này giúp Frontend (React/Vue/Mobile) xây dựng được các bộ chặn phản hồi
 * (Axios Interceptors) tập trung, tự động bóc tách tin nhắn thông báo mà không cần viết lại logic ở từng trang.
 * </p>
 *
 * @param timestamp Thời điểm chính xác xảy ra lỗi hệ thống trên Server (phục vụ mục đích tra cứu Log/Debug).
 * @param status    Giá trị số nguyên của mã trạng thái phản hồi HTTP (ví dụ: 400, 401, 409, 500).
 * @param error     Tên cụ thể của mã trạng thái HTTP tương ứng (ví dụ: "Bad Request", "Unauthorized", "Conflict").
 * @param message   Nội dung chuỗi tin nhắn mô tả lỗi chi tiết hoặc mã lỗi nội bộ đã bọc để hiển thị lên màn hình người dùng.
 * @param path      Đường dẫn URL (API Endpoint) của hệ thống đã kích hoạt và xảy ra lỗi (ví dụ: "/api/v1/auth/register").
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
@Builder
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
