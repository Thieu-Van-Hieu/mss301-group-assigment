package mss301.se1911.group.assignment.commonfeign.errordecoder;

/**
 * Giao diện thực thi hành vi cứu hộ hệ thống (Fallback Trigger Action).
 * <p>
 * Interface này được thiết kế theo phong cách Functional Interface (Mẫu Strategy),
 * cho phép các dịch vụ con tùy biến linh hoạt hành vi xử lý khi xảy ra lỗi dự phòng
 * (ví dụ: Ghi log định dạng đặc thù, bắn tin nhắn cảnh báo lỗi khẩn cấp tới Slack, Telegram).
 * </p>
 * <p>
 * <b>Cách tùy biến:</b> Lập trình viên tại từng Microservice cụ thể chỉ cần tạo một lớp
 * triển khai (implement) giao diện này và cấu hình tên lớp vào tham số của Annotation.
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
@FunctionalInterface
public interface FallbackTrigger {

    /**
     * Kích hoạt hành vi cứu hộ hệ thống khi luồng xử lý lỗi rơi vào kịch bản dự phòng.
     * <p>
     * Hàm này tự động được kích hoạt bởi bộ giải mã lỗi tập trung ngay khi toàn bộ
     * các cấu hình lọc lỗi bằng {@code @ErrorMapping} trên phương thức đều bị thất bại (không khớp).
     * </p>
     *
     * @param methodKey Tên phương thức hoặc Endpoint cụ thể của Feign Client bị sập lỗi (Dùng để định vị vị trí lỗi).
     * @param status    Mã trạng thái mạng HTTP thực tế nhận về từ hệ thống đối tác (ví dụ: 400, 404, 500).
     * @param errorBody Đối tượng dữ liệu chứa thân JSON lỗi thô đã được bóc tách dưới dạng Map. Có thể {@code null}.
     */
    void onFallbackTriggered(String methodKey, int status, Object errorBody);
}