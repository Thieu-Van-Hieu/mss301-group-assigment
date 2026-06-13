package mss301.se1911.group.assignment.commonfeign.annotation;

import org.springframework.http.HttpStatus;

import java.lang.annotation.*;

/**
 * Annotation dùng để cấu hình nghiệp vụ dịch lỗi trực tiếp trên từng phương thức gọi API.
 * <p>
 * Loại bỏ hoàn toàn việc viết các lớp Translator con thủ công. Lập trình viên chỉ cần
 * khai báo các thông số lỗi của hệ thống đích ngay trên đầu hàm Feign Client.
 * </p>
 * <p>
 * Annotation này có tính chất {@link Repeatable}, cho phép gắn nhiều cấu hình lỗi
 * khác nhau trên cùng một phương thức nếu phương thức đó dính nhiều kịch bản lỗi.
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @see ErrorMappings
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE}) // Chỉ cho phép gắn trên đầu Phương thức (Method) và Lớp (Type)
@Retention(RetentionPolicy.RUNTIME) // Giữ lại trong suốt quá trình ứng dụng chạy (Runtime)
@Documented // Xuất hiện trong tài liệu JavaDocs hệ thống
@Repeatable(ErrorMappings.class) // Chỉ định Thùng chứa số nhiều để cho phép lặp lại Annotation
public @interface ErrorMapping {

    /**
     * Mã trạng thái HTTP gốc nhận về từ cuộc gọi mạng từ xa (ví dụ: 400, 401, 409, 500).
     *
     * @return Mã số HTTP trạng thái dạng số nguyên nguyên thủy (int).
     */
    int status();

    /**
     * Từ khóa lỗi đặc thù nằm trong thân chuỗi phản hồi JSON của đối tác (ví dụ: "user_exists", "invalid_grant").
     * <p>Mặc định là chuỗi rỗng nếu hệ thống chỉ cần phân biệt lỗi dựa trên mã HTTP Status.</p>
     *
     * @return Chuỗi định danh lỗi đặc thù của bên thứ ba.
     */
    String errorKey() default "";

    /**
     * Mã trạng thái HTTP nghiệp vụ nội bộ muốn trả về ở bộ xử lý lỗi tập trung Global Advice.
     * (ví dụ: {@link HttpStatus#CONFLICT}, {@link HttpStatus#UNAUTHORIZED}).
     *
     * @return Đối tượng HttpStatus tương ứng của Framework Spring.
     */
    HttpStatus businessStatus();

    /**
     * Mã lỗi định danh viết liền viết hoa gửi ra ngoài cửa ngõ API cho Frontend lập trình logic.
     * (ví dụ: "USER_ALREADY_EXISTS", "INVALID_CREDENTIALS").
     *
     * @return Chuỗi mã lỗi nội bộ hệ thống.
     */
    String errorCode();

    /**
     * Tin nhắn thông báo lỗi chi tiết, dịch nghĩa thân thiện bằng tiếng Việt hiển thị cho người dùng cuối.
     *
     * @return Chuỗi nội dung tin nhắn lỗi.
     */
    String message();
}

