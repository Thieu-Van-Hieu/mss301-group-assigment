package mss301.se1911.group.assignment.commonfeign.errordecoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.commonfeign.annotation.EnableErrorTranslation;
import mss301.se1911.group.assignment.commonfeign.annotation.ErrorMapping;
import mss301.se1911.group.assignment.commonfeign.annotation.ErrorMappings;
import mss301.se1911.group.assignment.commonweb.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Bộ giải mã lỗi tổng quát tự động hóa xử lý dựa trên Annotation (Generic Error Decoder Engine).
 * <p>
 * Lớp này gánh vác toàn bộ các đoạn code trùng lặp (Boilerplate Code) liên quan đến việc
 * mở luồng mạng {@link InputStream}, bóc tách chuỗi JSON thô bằng {@link ObjectMapper},
 * và quản lý giải phóng tài nguyên hệ thống thông qua cơ chế {@code try-with-resources}.
 * </p>
 * <p>
 * <b>Cơ chế vận hành mặc định:</b> Khi OpenFeign dính lỗi HTTP không phải 2xx, lớp này sẽ dùng kỹ thuật
 * Reflection (Phản chiếu) để tìm đúng phương thức (Method) đang chạy bị lỗi, quét các cấu hình
 * định nghĩa lỗi nghiệp vụ trong Annotation {@link ErrorMapping} để tự động sinh và ném Exception.
 * </p>
 *
 * <h3>Hướng dẫn mở rộng, ghi đè hoặc định nghĩa lại (Redefine / Extends) tại từng Microservice:</h3>
 * Nếu một dịch vụ con (như Identity-Service hoặc Order-Service) có một số Endpoint vô cùng đặc thù
 * (ví dụ: lỗi không trả về JSON mà trả về XML, hoặc cần chạy thêm logic ghi nhận log database độc lập),
 * lập trình viên có thể thực hiện tùy biến theo 2 cách sau:
 *
 * <p><b>Cách 1: Ghi đè toàn cục cho một Client cụ thể thông qua file Java Config (Local Override)</b></p>
 * Tạo một lớp Decoder mới tại Service con kế thừa trực tiếp từ lớp gốc này, ghi đè hàm {@code decode}
 * và đăng ký nó cục bộ cho Feign Client mong muốn:
 * <pre>{@code
 * public class IdentityCustomDecoder extends GenericErrorDecoder {
 *     @Override
 *     public Exception decode(String methodKey, Response response) {
 *         if (methodKey.contains("specialEndpoint")) {
 *             // Viết logic xử lý đặc thù bằng tay tại đây
 *             return new CustomException("Lỗi đặc thù");
 *         }
 *         // Các endpoint khác gọi lại bộ quét Annotation tự động của lớp cha
 *         return super.decode(methodKey, response);
 *     }
 * }
 * }</pre>
 * Gán vào Client: {@code @FeignClient(name = "...", configuration = IdentityDecoderConfig.class)}
 *
 * <p><b>Cách 2: Ghi đè toàn cục bằng cách khai báo đè file YAML (YAML Precedence Override)</b></p>
 * Bạn vẫn viết một lớp con kế thừa từ {@code GenericErrorDecoder} như Cách 1, nhưng không cần tạo file Java Config.
 * Hãy chỉ định đích danh class con đó đè lên cấu hình mặc định trong file {@code application.yml}:
 * <pre>{@code
 * spring:
 *   cloud:
 *     openfeign:
 *       client:
 *         config:
 *           keycloak-auth-client:
 *             errorDecoder: mss301.se1911.group.assignment.identityservice.infrastructure.client.IdentityCustomDecoder
 * }</pre>
 *
 * @author Thiều Văn Hiếu
 * @see ErrorDecoder
 * @see AppException
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class GenericErrorDecoder implements ErrorDecoder {

    /**
     * Logger tĩnh dùng để in lịch sử xử lý và cảnh báo lỗi hạ tầng mạng ra màn hình Console.
     */
    private static final Logger logger = LoggerFactory.getLogger(GenericErrorDecoder.class);

    /**
     * Bộ giải mã lỗi mặc định nguyên bản của thư viện OpenFeign dùng làm phương án dự phòng (Fallback).
     */
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    /**
     * Công cụ Jackson dùng để chuyển đổi chuỗi JSON thô từ mạng thành đối tượng Map trong Java.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Kho chứa toàn bộ thông tin đối tượng (Beans Context) của Spring Boot.
     */
    private final ApplicationContext applicationContext;

    /**
     * Đánh chặn luồng lỗi HTTP thô của Feign Client và thực hiện chuyển đổi tự động dựa trên cấu hình Annotation.
     *
     * @param methodKey Chuỗi Text định danh duy nhất của hàm Feign bị lỗi (Định dạng: TênInterface#tênHàm(ThamSố)).
     * @param response  Đối tượng phản hồi HTTP chứa mã trạng thái, Header và thân Body lỗi từ Service đích gửi về.
     * @return Một Exception Java (Hoặc {@link AppException} nghiệp vụ hoặc {@code FeignException} mặc định).
     */
    @Override
    public Exception decode(String methodKey, Response response) {
        logger.error("[GenericErrorDecoder] Phát hiện lỗi HTTP từ Feign Client! MethodKey='{}', Status={}, Headers={}",
                methodKey, response.status(), response.headers());

        // Lấy mã trạng thái HTTP mạng thực tế (ví dụ: 400, 401, 409)
        int status = response.status();

        // Nếu Server đích báo lỗi nhưng thân Body trống rỗng, bàn giao cho bộ giải mã mặc định của Feign xử lý
        if (response.body() == null) return defaultDecoder.decode(methodKey, response);

        try {
            // =========================================================================
            // LUỒNG XỬ LÝ 1: THUẬT TOÁN SMART REFLECTION LOOKUP (SỬA LỖI CANNOT RESOLVE CLASS)
            // =========================================================================
            // Tách chuỗi methodKey thô tại dấu '#' (Kết quả: parts[0] = "KeycloakAuthClient")
            String[] parts = methodKey.split("#");
            String targetInterfaceShortName = parts[0];
            String methodSignature = parts[1];
            String targetMethodName = methodSignature.substring(0, methodSignature.indexOf("("));

            // Dò tìm chính xác Class Interface chứa Package đầy đủ dựa trên kho Bean của Spring
            Class<?> clientInterface = null;

            // Hỏi Spring xin toàn bộ danh sách các Class đang được gắn Annotation @FeignClient trong hệ thống
            Map<String, Object> feignClientBeans = applicationContext.getBeansWithAnnotation(FeignClient.class);

            for (Object beanInstance : feignClientBeans.values()) {
                // Duyệt qua danh sách Interface mà cái Bean này thực thi
                for (Class<?> iface : beanInstance.getClass().getInterfaces()) {
                    if (iface.getSimpleName().equals(targetInterfaceShortName)) {
                        // Tìm thấy Class Interface mang đường dẫn Package đầy đủ hợp pháp!
                        clientInterface = iface;
                        break;
                    }
                }
                if (clientInterface != null) break;
            }

            // Nếu đi hết kho Bean mà không tìm thấy Class (Fallback phòng ngừa), trả về lỗi thô
            if (clientInterface == null) return defaultDecoder.decode(methodKey, response);

            // Tìm chính xác đối tượng Method vật lý đang chạy bị lỗi bên trong Class vừa khôi phục
            Method targetMethod = null;
            for (Method method : clientInterface.getMethods()) {
                if (method.getName().equals(targetMethodName)) {
                    targetMethod = method;
                    break;
                }
            }
            if (targetMethod == null) return defaultDecoder.decode(methodKey, response);

            // =========================================================================
            // LUỒNG XỬ LÝ 2: GIẢI MÃ CHUỖI JSON LỖI THÔ QUA RAM (ĐỌC ĐỘC BẢN 1 LẦN)
            // =========================================================================
            Object errorDto;
            try (InputStream bodyIs = response.body().asInputStream()) {
                // Giải mã về Map thô vạn năng để triệt tiêu hoàn toàn rủi ro lỗi mập mờ Generic Class của Java
                errorDto = objectMapper.readValue(bodyIs, Map.class);
            }

            // Trích xuất mã lỗi thô từ Map (Hỗ trợ định dạng chuỗi "error" chuẩn của Keycloak)
            String actualErrorKey = (errorDto instanceof Map<?, ?> map && map.containsKey("error"))
                    ? String.valueOf(map.get("error"))
                    : "";

            // =========================================================================
            // LUỒNG XỬ LÝ 3: QUÉT ƯU TIÊN SỐ 1 - CẤP ĐỘ PHƯƠNG THỨC (METHOD LEVEL)
            // =========================================================================
            ErrorMapping[] methodMappings = getErrorMappingsFromElement(targetMethod);
            logger.info("[GenericErrorDecoder METHOD] Quét cấu hình lỗi trên phương thức: Found {} mappings", methodMappings.length);
            for (ErrorMapping mapping : methodMappings) {
                logger.info("[GenericErrorDecoder METHOD] Kiểm tra mapping: status={}, errorKey='{}'", mapping.status(), mapping.errorKey());
                if (mapping.status() == status && (mapping.errorKey().isEmpty() || mapping.errorKey().equalsIgnoreCase(actualErrorKey))) {
                    // Tìm thấy lỗi đặc thù được cấu hình riêng ở hàm -> Ném AppException luôn
                    throw new AppException(mapping.businessStatus(), mapping.errorCode(), mapping.message());
                }
            }

            // =========================================================================
            // LUỒNG XỬ LÝ 4: QUÉT ƯU TIÊN SỐ 2 - CẤP ĐỘ GIAO DIỆN (INTERFACE LEVEL GLOBAL)
            // =========================================================================
            // Nếu đi hết danh sách Method mà không ai nhận, tiến hành quét lên đầu Interface xem có cấu hình lỗi chung không
            ErrorMapping[] interfaceMappings = getErrorMappingsFromElement(clientInterface);
            logger.info("[GenericErrorDecoder TYPE] Quét cấu hình lỗi trên Interface: Found {} mappings", interfaceMappings.length);
            for (ErrorMapping mapping : interfaceMappings) {
                logger.info("[GenericErrorDecoder TYPE] Kiểm tra mapping: status={}, errorKey='{}'", mapping.status(), mapping.errorKey());
                if (mapping.status() == status && (mapping.errorKey().isEmpty() || mapping.errorKey().equalsIgnoreCase(actualErrorKey))) {
                    // Tìm thấy lỗi cấu hình toàn cục (như 401, 403) được khai báo tập trung ở đầu Interface!
                    throw new AppException(mapping.businessStatus(), mapping.errorCode(), mapping.message());
                }
            }

            // =========================================================================
            // LUỒNG XỬ LÝ 5: THIẾT LẬP THÔNG SỐ VÀ KÍCH HOẠT HÀNH VI FALLBACK TRIGGER CUSTOM
            // =========================================================================
            // Dựng các giá trị cấu hình mặc định toàn cục cho kịch bản dự phòng
            HttpStatus finalFallbackStatus = HttpStatus.BAD_GATEWAY;
            String finalFallbackErrorCode = "EXTERNAL_SYSTEM_ERROR";
            String finalFallbackMessage = "Hệ thống liên kết liên dịch vụ xảy ra sự cố không xác định.";
            Class<? extends FallbackTrigger> triggerClass = EnableErrorTranslation.DefaultFallbackLogger.class;

            // 💡 KIỂM TRA REDEFINE: Nếu lập trình viên có gắn cấu hình tùy biến trên đầu Interface
            if (clientInterface.isAnnotationPresent(EnableErrorTranslation.class)) {
                EnableErrorTranslation config = clientInterface.getAnnotation(EnableErrorTranslation.class);
                finalFallbackStatus = config.fallbackStatus();
                finalFallbackErrorCode = config.fallbackErrorCode();
                finalFallbackMessage = config.fallbackMessage();
                triggerClass = config.fallbackTrigger(); // Lấy Class hành vi được Redefine ra
            }

            // 💡 KHỞI TẠO ĐỘNG VÀ KÍCH HOẠT HÀNH VI LOG/ALERT TÙY BIẾN QUA REFLECTION
            try {
                // Khởi tạo nhanh instance của Class cứu hộ bằng hàm constructor không tham số ngầm định
                FallbackTrigger triggerInstance = triggerClass.getDeclaredConstructor().newInstance();
                // Kích hoạt thực thi hàm xử lý
                triggerInstance.onFallbackTriggered(methodKey, status, errorDto);
            } catch (Exception ex) {
                logger.error("[Framework Core Error] Thất bại khi khởi tạo động hành vi Fallback Trigger Class Custom", ex);
            }

            // Ném ngoại lệ Fallback mặc định lên tầng Advice
            throw new AppException(finalFallbackStatus, finalFallbackErrorCode, finalFallbackMessage);

        } catch (AppException e) {
            logger.warn("[Business Exception] Phát sinh lỗi nghiệp vụ được cấu hình trong Annotation: {}", e.getMessage());
            // Trả ngoại lệ nghiệp vụ thuần khiết về luồng kết thúc của OpenFeign để bắn trôi lên trên
            return e;
        } catch (Exception e) {
            logger.error("[Critical Framework Error] Phát sinh lỗi nghiêm trọng trong quá trình giải mã lỗi của GenericErrorDecoder!", e);
            // Dự phòng sự cố Reflection nghiêm trọng, bàn giao trả về lỗi mạng thô của Feign
            return defaultDecoder.decode(methodKey, response);
        }
    }

    /**
     * Hàm tiện ích Generic trích xuất danh sách Annotation hỗ trợ vạn năng cho cả Class (Interface) và Method.
     *
     * @param element Đối tượng phản chiếu chung có thể là Class hoặc Method cần trích xuất Annotation.
     * @return Mảng chứa toàn bộ các đối tượng {@link ErrorMapping} hợp lệ tìm thấy trên phần tử được chỉ định (Có thể là cấp độ Interface hoặc Method).
     */
    private ErrorMapping[] getErrorMappingsFromElement(AnnotatedElement element) {
        if (element.isAnnotationPresent(ErrorMappings.class)) {
            return element.getAnnotation(ErrorMappings.class).value();
        } else if (element.isAnnotationPresent(ErrorMapping.class)) {
            return new ErrorMapping[]{element.getAnnotation(ErrorMapping.class)};
        }
        return new ErrorMapping[0];
    }
}

