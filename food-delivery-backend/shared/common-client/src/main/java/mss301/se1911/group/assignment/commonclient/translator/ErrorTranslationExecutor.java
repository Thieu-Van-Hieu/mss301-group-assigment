package mss301.se1911.group.assignment.commonclient.translator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mss301.se1911.group.assignment.commonclient.exceptioin.ErrorTranslator;
import mss301.se1911.group.assignment.commonweb.exception.AppException;

/**
 * Công cụ thực thi dịch lỗi tập trung (Error Translation Executor).
 * <p>
 * Hỗ trợ duyệt qua danh sách các bộ dịch lỗi Generic, tự động kiểm tra điều kiện áp dụng,
 * ném ra ngoại lệ nghiệp vụ tương ứng hoặc kích hoạt cơ chế báo lỗi mặc định (Fallback).
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorTranslationExecutor {
    /**
     * Thực thi duyệt bộ dịch lỗi dựa trên ngữ cảnh đã cung cấp và chủ động ném {@link AppException}.
     *
     * @param <T>     Kiểu dữ liệu cấu trúc JSON lỗi đặc thù của hệ thống đích.
     * @param context Đối tượng Record chứa toàn bộ thông số mạng và cấu hình dự phòng.
     * @throws AppException Luôn luôn ném ra ngoại lệ nghiệp vụ (Hoặc từ bộ dịch khớp hoặc từ bộ dự phòng).
     */
    public static <T> void executeAndThrow(ErrorTranslationContext<T> context) {

        // 1. Duyệt tìm bộ dịch lỗi phù hợp thông qua dữ liệu trích xuất từ Record
        if (context.translators() != null) {
            for (ErrorTranslator<T> translator : context.translators()) {
                if (translator.isApplicable(context.status(), context.responseBody())) {
                    throw translator.translate(context.responseBody());
                }
            }
        }

        // 2. KÍCH HOẠT CALLBACK LOG CẢNH BÁO TRƯỚC KHI TOANG (Nếu có cấu hình)
        if (context.onFallbackTriggered() != null) {
            context.onFallbackTriggered().accept(context.responseBody());
        }

        // 3. Cơ chế Fallback mặc định
        throw new AppException(
                context.fallbackStatus(),
                context.fallbackErrorCode(),
                context.fallbackMessage()
        );
    }
}
