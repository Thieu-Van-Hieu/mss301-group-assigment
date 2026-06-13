# OpenFeign & HTTP Client Advanced Error Translation System

Bộ thư viện Core Utility hỗ trợ **tự động hóa dịch lỗi nghiệp vụ** toàn diện cho giao tiếp liên dịch vụ (Inter-service
Communication). Thư viện cung cấp song song hai cơ chế xử lý mạnh mẽ:

1. **Declarative Engine (Dựa trên Annotation):** Dành riêng cho OpenFeign Client qua cơ chế Auto-configuration ngầm
   định.
2. **Programmatic Engine (Dựa trên Chiến lược Code):** Áp dụng mẫu thiết kế **Strategy Pattern**, dành cho các HTTP
   Client khác như `RestTemplate`, `WebClient` hoặc ứng dụng xử lý thủ công linh hoạt.

---

## 🚀 Tính năng cốt lõi bổ sung (Programmatic Error Engine)

* **Mẫu thiết kế Chiến lược (Strategy Pattern):** Tách biệt hoàn toàn cấu trúc JSON lỗi phức tạp của từng đối tác bên
  thứ ba ra khỏi logic xử lý nghiệp vụ chính nhờ `ErrorTranslator<T>`.
* **Quản lý ngữ cảnh qua Bản ghi (Context Record Management):** Gom toàn bộ dữ liệu phản hồi mạng, danh sách bộ dịch
  tiềm năng và kịch bản dự phòng (`Fallback`) vào một đối tượng `ErrorTranslationContext` bất biến, an toàn đa luồng.
* **Bộ thực thi tập trung (Centralized Executor Engine):** `ErrorTranslationExecutor` quản lý vòng đời quét lỗi tuần tự,
  tự động kích hoạt callback cảnh báo khẩn cấp (Slack/Telegram) trước khi hệ thống kích hoạt kịch bản sập nguồn an toàn.

---

## 🛠️ Chi tiết các thành phần chính

### 1. `ErrorTranslator<T>`

Interface đại diện cho một chiến lược dịch lỗi cụ thể cho một cấu trúc dữ liệu JSON (`T`) xác định từ bên ngoài.

* **Phương thức:**
* `boolean isApplicable(int status, T errorResponseBody);`: Kiểm tra điều kiện (kết hợp HTTP Status và dữ liệu lỗi thô)
  xem bộ dịch này có khớp kịch bản lỗi hiện tại hay không.
* `AppException translate(T errorResponseBody);`: Định hình cấu trúc và ánh xạ trực tiếp sang ngoại lệ hệ thống nội bộ
  `AppException`.

### 2. `ErrorTranslationExecutor`

Lớp tiện ích tối cao (`Utility Final Class`) chịu trách nhiệm duyệt qua tập hợp chiến lược, điều phối luồng ném lỗi hoặc
thực hiện nạp thông số dự phòng tự động.

---

## 💡 Hướng dẫn áp dụng thực tế (Không dùng OpenFeign)

Giả sử hệ thống của bạn cần gọi một API tích hợp từ đối tác bên thứ ba bằng `RestTemplate` và đối tác trả về cấu trúc dữ
liệu lỗi chuẩn dạng JSON như sau:

```json
{
    "error_code": "SYSTEM_BUSY",
    "error_description": "Server dang qua tai"
}

```

### Bước 1: Khai báo DTO đại diện cấu trúc lỗi đối tác

```java
package mss301.se1911.group.assignment.orderservice.dto;

public record PartnerErrorResponse(String error_code, String error_description) {
}

```

### Bước 2: Hiện thực chiến lược dịch lỗi (`Implement Strategy`)

Tạo các lớp xử lý lỗi độc lập cho từng kịch bản nghiệp vụ đặc thù của đối tác:

```java
package mss301.se1911.group.assignment.orderservice.translator;

import mss301.se1911.group.assignment.commonclient.exceptioin.ErrorTranslator;
import mss301.se1911.group.assignment.commonweb.exception.AppException;
import mss301.se1911.group.assignment.orderservice.dto.PartnerErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PartnerSystemBusyTranslator implements ErrorTranslator<PartnerErrorResponse> {

    @Override
    public boolean isApplicable(int status, PartnerErrorResponse body) {
        // Áp dụng nếu đối tác trả về HTTP 503 và mã lỗi nội bộ là SYSTEM_BUSY
        return status == 503 && body != null && "SYSTEM_BUSY".equals(body.error_code());
    }

    @Override
    public AppException translate(PartnerErrorResponse body) {
        return new AppException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "PARTNER_SYSTEM_DOWN",
                "Hệ thống đối tác liên kết đang bận: " + body.error_description()
        );
    }
}

```

### Bước 3: Định nghĩa Ngữ cảnh Record chứa dữ liệu (`Context Definition`)

*(Lưu ý: Bạn cần tạo cấu trúc `ErrorTranslationContext` dạng Record chứa các trường tương ứng với mã
nguồn `ErrorTranslationExecutor` yêu cầu).*

```java
package mss301.se1911.group.assignment.commonclient.translator;

import mss301.se1911.group.assignment.commonclient.exceptioin.ErrorTranslator;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.function.Consumer;

public record ErrorTranslationContext<T>(
        int status,
        T responseBody,
        List<ErrorTranslator<T>> translators,
        HttpStatus fallbackStatus,
        String fallbackErrorCode,
        String fallbackMessage,
        Consumer<T> onFallbackTriggered
) {
}

```

### Bước 4: Gọi thực thi tích hợp tại Tầng nghiệp vụ (`Service Layer`)

Sử dụng `ErrorTranslationExecutor` trong khối bắt ngoại lệ (`catch`) của `RestTemplate` để tự động hóa khâu xử lý lỗi
nghiệp vụ:

```java
package mss301.se1911.group.assignment.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonclient.exceptioin.ErrorTranslator;
import mss301.se1911.group.assignment.commonclient.translator.ErrorTranslationContext;
import mss301.se1911.group.assignment.commonclient.translator.ErrorTranslationExecutor;
import mss301.se1911.group.assignment.orderservice.dto.PartnerErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RestTemplate restTemplate;
    private final List<ErrorTranslator<PartnerErrorResponse>> partnerTranslators; // Inject toàn bộ chiến lược dịch lỗi

    public void processPayment() {
        try {
            restTemplate.postForObject("https://api.partner.com/v1/pay", null, String.class);
        } catch (HttpServerErrorException ex) {
            // 1. Trích xuất cơ thể lỗi thô từ Gateway đối tác
            PartnerErrorResponse rawError = ex.getResponseBodyAs(PartnerErrorResponse.class);

            // 2. Thiết lập cấu hình ngữ cảnh dịch lỗi bất biến
            ErrorTranslationContext<PartnerErrorResponse> context = new ErrorTranslationContext<>(
                    ex.getStatusCode().value(),
                    rawError,
                    partnerTranslators,
                    HttpStatus.BAD_GATEWAY,
                    "PAYMENT_SERVICE_FALLBACK_ERROR",
                    "Cổng kết nối thanh toán xảy ra lỗi hệ thống nghiêm trọng.",
                    body -> log.error("[ALERT CHANNELS] Đang kích hoạt thông báo khẩn cấp lên Slack! Lỗi thô: {}", body)
            );

            // 3. Thực thi quét chiến lược và tự động ném AppException tương ứng
            ErrorTranslationExecutor.executeAndThrow(context);
        }
    }
}

```

---

## 📦 Đóng gói Auto-Configuration (Sử dụng song song 2 cơ chế)

Khi tích hợp bộ mã nguồn này vào chung thư viện Core Starter, lớp cấu hình tự động của bạn sẽ bao quát toàn bộ hệ thống
xử lý lỗi mạng:

```java
package mss301.se1911.group.assignment.commonfeign.config;

import feign.codec.ErrorDecoder;
import mss301.se1911.group.assignment.commonfeign.errordecoder.GenericErrorDecoder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FeignErrorTranslationAutoConfiguration {

    /**
     * Cơ chế 1: Tự động kích hoạt cho toàn bộ dự án xài OpenFeign
     */
    @Bean
    @ConditionalOnClass(ErrorDecoder.class)
    @ConditionalOnMissingBean(ErrorDecoder.class)
    public ErrorDecoder errorDecoder() {
        return new GenericErrorDecoder();
    }

    // Note: Do `ErrorTranslationExecutor` sử dụng các phương thức Static Utility,
    // người dùng có thể gọi trực tiếp ở bất cứ đâu trong dự án mà không cần khai báo Spring Bean.
}

```

Khai báo nạp tự động thông qua tệp tin SPI:
`src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

```text
mss301.se1911.group.assignment.commonfeign.config.FeignErrorTranslationAutoConfiguration

```

---

**Author:** Thiều Văn Hiếu

**Since:** 1.0.0

**Target Core Framework:** Spring Boot 3.x, Cloud Core API Utilities