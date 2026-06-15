package mss301.se1911.group.assignment.identityservice.infrastructure.client.keycloak;

import mss301.se1911.group.assignment.commonfeign.annotation.ErrorMapping;
import mss301.se1911.group.assignment.commonsecurity.dto.response.TokenResponse;
import mss301.se1911.group.assignment.commonsecurity.dto.response.UserValidateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "keycloak-auth-client")
@ErrorMapping(
        status = 401, businessStatus = HttpStatus.UNAUTHORIZED, errorKey = "invalid_credentials", errorCode = "INVALID_CREDENTIALS", message = "Lỗi cấu hình xác thực nội bộ của hệ thống.")
public interface KeycloakAuthClient {
    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    TokenResponse tokenEndpoint(
            @PathVariable("realm") String realm,
            Map<String, ?> formData
    );

    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/token/introspect",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    UserValidateResponse introspectEndpoint(
            @PathVariable("realm") String realm,
            Map<String, ?> formData
    );

    @PostMapping(
            value = "/realms/{realm}/protocol/openid-connect/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    @ErrorMapping(
            status = 400,
            errorKey = "invalid_grant",
            businessStatus = HttpStatus.BAD_REQUEST,
            errorCode = "TOKEN_NOT_ACTIVE",
            message = "Phiên làm việc (Refresh Token) đã hết hạn hoặc đã được sử dụng."
    )
    TokenResponse refreshEndpoint(
            @PathVariable("realm") String realm,
            Map<String, ?> formData
    );
}
