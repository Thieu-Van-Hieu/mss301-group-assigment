## 💡 Hướng dẫn sử dụng dành cho End-User (Các Microservice con)

### Bước 1: Thêm Dependency vào dự án của bạn

Người dùng chỉ cần nhúng thư viện vào dự án (`pom.xml` của Maven hoặc `build.gradle` của Gradle). Do đã cấu hình SPI ở
trên, bộ giải mã lỗi sẽ **tự động chạy ngầm**.

```xml

<dependency>
	<groupId>mss301.se1911.group.assignment</groupId>
	<artifactId>common-feign</artifactId>
</dependency>

```

### Bước 2: Khai báo xử lý lỗi trên Feign Client

Lập trình viên chỉ cần tập trung sử dụng các Annotation để khai báo kịch bản nghiệp vụ trực tiếp trên các Interface
Feign Client mà không cần viết thêm bất kỳ dòng code cấu hình nào.

```java
package mss301.se1911.group.assignment.identityservice.infrastructure.client;

import mss301.se1911.group.assignment.commonfeign.annotation.EnableErrorTranslation;
import mss301.se1911.group.assignment.commonfeign.annotation.ErrorMapping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "keycloak-auth-client", url = "${identity.keycloak.url}")
@EnableErrorTranslation(
        fallbackErrorCode = "IDENTITY_PROVIDER_DOWN",
        fallbackMessage = "Hệ thống xác thực phân quyền gặp sự cố, vui lòng thử lại sau."
)
// 1. Cấu hình lỗi chung áp dụng cho TOÀN BỘ phương thức trong Interface này (Cấp độ Interface)
@ErrorMapping(status = 401, businessStatus = HttpStatus.UNAUTHORIZED, errorCode = "UNAUTHORIZED_REQUEST", message = "Phiên làm việc đã hết hạn.")
@ErrorMapping(status = 403, businessStatus = HttpStatus.FORBIDDEN, errorCode = "ACCESS_DENIED", message = "Bạn không có quyền thực hiện hành động này.")
public interface KeycloakAuthClient {

    @PostMapping("/protocol/openid-connect/token")
    // 2. Cấu hình lỗi chi tiết dành riêng cho phương thức này (Ưu tiên số 1 - Cấp độ Method)
    @ErrorMapping(status = 400, errorKey = "invalid_grant", businessStatus = HttpStatus.BAD_REQUEST, errorCode = "INVALID_CREDENTIALS", message = "Tài khoản hoặc mật khẩu không chính xác.")
    @ErrorMapping(status = 409, errorKey = "user_exists", businessStatus = HttpStatus.CONFLICT, errorCode = "USERNAME_ALREADY_TAKEN", message = "Tên tài khoản này đã tồn tại trên hệ thống.")
    Map<String, Object> introspectEndpoint(@RequestBody Map<String, ?> loginData);
}

```

---

## 🔄 Hướng dẫn mở rộng & Ghi đè cấu hình (Mọi thứ vẫn hoạt động hoàn hảo)

Nhờ vào từ khóa `@ConditionalOnMissingBean(ErrorDecoder.class)` được cấu hình sẵn trong Starter, khi một dịch vụ con có
các Endpoint vô cùng đặc thù cần xử lý thủ công, bạn hoàn toàn có thể ghi đè bộ giải mã tự động bằng 2 cách:

### Cách 1: Ghi đè bằng mã Java (Local Java Configuration)

Tạo một lớp Decoder mới kế thừa từ `GenericErrorDecoder` tại Service con, xử lý thủ công trường hợp đặc biệt và gọi
`super.decode` cho các trường hợp còn lại:

```java
public class IdentityCustomDecoder extends GenericErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (methodKey.contains("specialEndpoint")) {
            // Tự xử lý bóc tách cấu hình lỗi đặc biệt tại đây
            return new CustomException("Lỗi cấu trúc phản hồi đặc thù");
        }
        return super.decode(methodKey, response);
    }
}

```

Sau đó đăng ký cục bộ qua thuộc tính `configuration` trên Client mong muốn:

```java
@FeignClient(name = "keycloak-client", configuration = IdentityDecoderConfig.class)

```

### Cách 2: Ghi đè cấu hình thông qua file Application YAML

Chỉ định đích danh class con kế thừa đè lên cấu hình mặc định trong file `application.yml` của service con:

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          keycloak-auth-client:
            errorDecoder: mss301.se1911.group.assignment.identityservice.infrastructure.client.IdentityCustomDecoder

```

---

## 🪵 Minh họa Log Cảnh báo từ Hệ thống (`DefaultFallbackLogger`)

Khi đối tác trả về mã lỗi kết nối nhưng lập trình viên **chưa khai báo** Annotation `@ErrorMapping` để giải mã, hệ thống
cứu hộ mặc định sẽ in chỉ dẫn sửa lỗi chuẩn hóa ra Console để hỗ trợ quá trình debug nhanh chóng:

```text
[ERROR] [CRITICAL WARNING] Phát hiện lỗ hổng hệ thống: THIẾU BỘ DỊCH LỖI (ErrorTranslator)!
[ERROR] -> Vị trí phương thức Feign Client bị lỗi: mss301.se1911.group.assignment.identityservice.infrastructure.client.KeycloakAuthClient#introspectEndpoint(Map)
[ERROR] -> HTTP Status Code đối tác trả về: 500
[ERROR] -> Dữ liệu JSON thô chưa được dịch nghiệp vụ: {error=internal_server_error, error_description=Database connection timeout on remote server}
[ERROR] -> Vui lòng bổ sung thêm Annotation @ErrorMapping trên phương thức này để giải mã mã lỗi nghiệp vụ.

```

---

**Author:** Thiều Văn Hiếu

**Since:** 1.0.0

**Framework Version Base:** Spring Boot 3.x, Spring Cloud OpenFeign 4.x

```

```