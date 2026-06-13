package mss301.se1911.group.assignment.commonclient.exceptioin;

import mss301.se1911.group.assignment.commonweb.exception.AppException;

/**
 * Giao diện dịch lỗi tổng quát (Generic Error Translator) dành cho các HTTP Client.
 * <p>
 * Interface này áp dụng mẫu thiết kế Strategy (Callback) nhằm mục đích phân rã logic
 * xử lý lỗi thô từ các cuộc gọi API bên ngoài (OpenFeign, RestTemplate, WebClient)
 * thành các ngoại lệ nghiệp vụ nội bộ ứng dụng.
 * </p>
 * <p>
 * <b>Cách sử dụng:</b> Mỗi phân hệ hoặc dịch vụ đích khi có cấu trúc lỗi riêng biệt
 * sẽ triển khai (implement) giao diện này kèm theo DTO tương ứng của hệ thống đó.
 * </p>
 *
 * @param <T> Kiểu dữ liệu (Class DTO) đại diện cho cấu trúc JSON phản hồi lỗi của hệ thống đích.
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
public interface ErrorTranslator<T> {

    /**
     * Hàm gọi lại (Callback) dùng để tự nhận diện xem bộ dịch này có khả năng xử lý lỗi hiện tại hay không.
     * <p>
     * Logic nhận diện thường dựa trên sự kết hợp giữa mã trạng thái mạng HTTP
     * và các từ khóa lỗi (error key/code) nằm trong thân phản hồi JSON.
     * </p>
     *
     * @param status            Mã trạng thái HTTP gốc nhận về từ Client (ví dụ: 400, 401, 409, 500).
     * @param errorResponseBody Đối tượng Java đại diện cho JSON lỗi thô đã được giải mã tự động. Có thể {@code null}.
     * @return {@code true} nếu bộ dịch này khớp và đồng ý xử lý lỗi nghiệp vụ này, ngược lại trả về {@code false}.
     */
    boolean isApplicable(int status, T errorResponseBody);

    /**
     * Thực hiện chuyển đổi và ánh xạ thông tin lỗi thô thành ngoại lệ nghiệp vụ chuẩn của hệ thống.
     * <p>
     * Hàm này chỉ được kích hoạt sau khi {@link #isApplicable(int, Object)} đã xác nhận thành công (trả về {@code true}).
     * </p>
     *
     * @param errorResponseBody Đối tượng Java chứa dữ liệu lỗi chi tiết từ hệ thống đích để trích xuất tin nhắn hoặc mã nội bộ.
     * @return Một đối tượng {@link AppException} mang tính nghiệp vụ tinh khiết, sẵn sàng để ném ra ngoài tầng điều khiển.
     */
    AppException translate(T errorResponseBody);
}
