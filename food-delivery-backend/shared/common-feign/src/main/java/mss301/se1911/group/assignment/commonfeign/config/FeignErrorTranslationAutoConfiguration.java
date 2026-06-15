package mss301.se1911.group.assignment.commonfeign.config;

import feign.codec.ErrorDecoder;
import mss301.se1911.group.assignment.commonfeign.errordecoder.GenericErrorDecoder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Lớp cấu hình tự động toàn cục kích hoạt cơ chế dịch lỗi qua Annotation cho hệ thống OpenFeign.
 * <p>
 * Lớp này đóng vai trò là một thành phần cốt lõi của Custom Starter (Pluggable Starter).
 * Khi bất kỳ Microservice nào nạp module thư viện này vào hệ thống và kích hoạt quét qua thư mục
 * {@code META-INF}, Spring Boot sẽ tự động nạp cấu hình dịch lỗi này ngầm định.
 * </p>
 * <p>
 * Điều kiện kích hoạt:
 * Chỉ khởi tạo cấu hình này nếu trong Classpath của dự án có tồn tại class {@link ErrorDecoder} của thư viện Feign.
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
@AutoConfiguration // Đánh dấu đây là class cấu hình tự động chuẩn của Spring Boot 3.x
@ConditionalOnClass(ErrorDecoder.class) // Kích hoạt nếu dự án có sử dụng OpenFeign
public class FeignErrorTranslationAutoConfiguration {

    /**
     * Khởi tạo và đăng ký bộ giải mã lỗi thông minh tập trung GenericErrorDecoder vào Spring Context.
     * <p>
     * Hàm này áp dụng điều kiện {@link ConditionalOnMissingBean}. Nếu tại Microservice con
     * lập trình viên tự viết một Bean {@link ErrorDecoder} tùy biến riêng của họ, Spring Boot
     * sẽ ưu tiên dùng Bean của họ và tự động vô hiệu hóa (bỏ qua) Bean mặc định này của thư viện.
     * </p>
     *
     * @param applicationContext Kho chứa toàn bộ thông tin đối tượng Beans nội bộ của Spring Boot
     *                           (Được Spring tự động tìm kiếm và tiêm vào hàm tại thời điểm Runtime).
     * @return Một instance của {@link GenericErrorDecoder} đã bọc đầy đủ ngữ cảnh hạ tầng Spring.
     */
    @Bean
    @ConditionalOnMissingBean(ErrorDecoder.class) // Chỉ cấu hình nếu hệ thống chưa có Bean ErrorDecoder nào khác
    public ErrorDecoder errorDecoder(ApplicationContext applicationContext) {
        // 💡 GIẢI PHÁP ĐỒNG BỘ: Truyền chính xác applicationContext được Spring cấp phát vào hàm khởi tạo
        return new GenericErrorDecoder(applicationContext);
    }
}

