package mss301.se1911.group.assignment.commonfeign.annotation;

import mss301.se1911.group.assignment.commonfeign.errordecoder.FallbackTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.lang.annotation.*;

/**
 * Annotation kích hoạt cơ chế tự động dịch lỗi và tùy biến (Redefine) thông số dự phòng cho Feign Client.
 * <p>
 * Gắn trên đầu Giao diện (Interface) nhằm mục đích định nghĩa lại cấu trúc ngoại lệ mặc định
 * và hành vi Robot cứu hộ khi luồng xử lý bị rơi vào kịch bản dự phòng (Fallback).
 * </p>
 * <p>
 * <b>Nguyên lý Convention over Configuration:</b> Annotation này KHÔNG BẮT BUỘC phải khai báo.
 * Nếu lập trình viên bỏ trống, hệ thống sẽ tự động nạp toàn bộ cấu hình mặc định an toàn toàn cục.
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
@Target(ElementType.TYPE) // Chỉ cho phép gắn trên cấu trúc Lớp hoặc Giao diện (Interface)
@Retention(RetentionPolicy.RUNTIME) // Giữ lại trong bộ nhớ lúc ứng dụng đang chạy để Reflection quét được
@Documented
public @interface EnableErrorTranslation {

    /**
     * Định nghĩa mã lỗi định danh nội bộ dự phòng (Fallback Error Code) trả về cho Frontend khi không có bộ dịch nào khớp.
     *
     * @return Chuỗi mã lỗi nội bộ mặc định (Mặc định giá trị: "EXTERNAL_SYSTEM_ERROR").
     */
    String fallbackErrorCode() default "EXTERNAL_SYSTEM_ERROR";

    /**
     * Định nghĩa tin nhắn thông báo lỗi dự phòng hiển thị cho người dùng cuối khi cơ chế dịch lỗi nghiệp vụ thất bại.
     *
     * @return Chuỗi văn bản tin nhắn thân thiện bằng tiếng Việt.
     */
    String fallbackMessage() default "Hệ thống liên kết liên dịch vụ xảy ra sự cố không xác định.";

    /**
     * Định nghĩa mã trạng thái HTTP dự phòng trả về ở cửa ngõ API khi luồng rơi vào kịch bản mặc định.
     *
     * @return Đối tượng HttpStatus chuẩn của Spring (Mặc định giá trị: {@link HttpStatus#BAD_GATEWAY}).
     */
    HttpStatus fallbackStatus() default HttpStatus.BAD_GATEWAY;

    /**
     * Định nghĩa và cấu hình lại Class đảm nhận hành vi ghi Log/Alert cứu hộ khi hệ thống dính lỗi Fallback.
     * <p>
     * Hỗ trợ cắm các bộ xử lý đẩy thông tin lỗi sang các kênh bên thứ ba (Slack, Telegram) một cách độc lập.
     * </p>
     *
     * @return Lớp Class thực thi giao diện {@link FallbackTrigger} (Mặc định dùng {@link DefaultFallbackLogger}).
     */
    Class<? extends FallbackTrigger> fallbackTrigger() default DefaultFallbackLogger.class;

    /**
     * Bộ ghi nhận nhật ký lỗi mặc định của Framework (Default Fallback Engine Logger).
     * <p>
     * Tự động kích hoạt in hướng dẫn xử lý lỗi chi tiết và dữ liệu JSON thô ra màn hình Console
     * nếu lập trình viên không có nhu cầu tùy biến Class hành vi.
     * </p>
     */
    class DefaultFallbackLogger implements FallbackTrigger {
        private static final Logger log = LoggerFactory.getLogger(DefaultFallbackLogger.class);

        /**
         * Thực hiện ghi nhận thông tin chỉ dẫn sửa lỗi cấu trúc chuẩn của Core Framework.
         *
         * @param methodKey Tên phương thức hoặc Endpoint bị sập lỗi.
         * @param status    Mã trạng thái mạng HTTP thực tế nhận về từ hệ thống đối tác.
         * @param errorBody Đối tượng dữ liệu chứa thân JSON lỗi thô dưới dạng Map.
         */
        @Override
        public void onFallbackTriggered(String methodKey, int status, Object errorBody) {
            log.error("[CRITICAL WARNING] Phát hiện lỗ hổng hệ thống: THIẾU BỘ DỊCH LỖI (ErrorTranslator)!");
            log.error("-> Vị trí phương thức Feign Client bị lỗi: {}", methodKey);
            log.error("-> HTTP Status Code đối tác trả về: {}", status);
            if (errorBody != null) {
                log.error("-> Dữ liệu JSON thô chưa được dịch nghiệp vụ: {}", errorBody);
            }
            log.error("-> Vui lòng bổ sung thêm Annotation @ErrorMapping trên phương thức này để giải mã mã lỗi nghiệp vụ.");
        }
    }
}