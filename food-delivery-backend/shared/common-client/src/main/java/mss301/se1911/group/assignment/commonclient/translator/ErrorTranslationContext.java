package mss301.se1911.group.assignment.commonclient.translator;

import lombok.Builder;
import mss301.se1911.group.assignment.commonclient.exceptioin.ErrorTranslator;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.function.Consumer;

/**
 * Đối tượng bọc ngữ cảnh dịch lỗi (Error Translation Context).
 * <p>
 * Record này đóng vai trò là một thùng chứa bất biến bọc toàn bộ các tham số mạng
 * và cấu hình dự phòng (Fallback) cần thiết phục vụ cho quy trình dịch lỗi.
 * </p>
 *
 * @param <T> Kiểu dữ liệu cấu trúc JSON lỗi đặc thù của hệ thống đích.
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
@Builder // 💡 Lombok tự động tạo ra một bộ Fluent Builder hoàn hảo cho Record này
public record ErrorTranslationContext<T>(
        List<ErrorTranslator<T>> translators,
        int status,
        T responseBody,
        HttpStatus fallbackStatus,
        String fallbackErrorCode,
        String fallbackMessage,
        Consumer<T> onFallbackTriggered
) {
}

