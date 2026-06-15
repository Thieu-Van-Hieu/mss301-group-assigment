package mss301.se1911.group.assignment.commonfeign.annotation;

import java.lang.annotation.*;

/**
 * Thùng chứa (Container Annotation) phục vụ cho tính năng lặp lại của {@link ErrorMapping}.
 * <p>
 * Theo quy định của Java, để một Annotation có thể gắn trùng nhau nhiều lần trên một phương thức,
 * bắt buộc phải có một Annotation thùng chứa bọc ngoài để quản lý danh sách dưới dạng mảng.
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE}) // Chỉ gắn trên phương thức và lớp, tương tự như ErrorMapping
@Retention(RetentionPolicy.RUNTIME) // Giữ lại lúc hệ thống chạy
@Documented
public @interface ErrorMappings {

    /**
     * Mảng chứa danh sách toàn bộ các cấu hình xử lý lỗi độc lập được khai báo lặp lại trên hàm.
     *
     * @return Mảng các đối tượng {@link ErrorMapping}.
     */
    ErrorMapping[] value();
}

