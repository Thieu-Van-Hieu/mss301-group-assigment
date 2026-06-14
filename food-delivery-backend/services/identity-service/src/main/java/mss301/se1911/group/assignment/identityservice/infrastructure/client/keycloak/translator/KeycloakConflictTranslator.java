package mss301.se1911.group.assignment.identityservice.infrastructure.client.keycloak.translator;

import mss301.se1911.group.assignment.commonclient.exceptioin.ErrorTranslator;
import mss301.se1911.group.assignment.commonweb.exception.AppException;
import mss301.se1911.group.assignment.identityservice.api.dto.response.KeycloakErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConflictTranslator implements ErrorTranslator<KeycloakErrorResponse> {

    @Override
    public boolean isApplicable(int status, KeycloakErrorResponse response) {
        return status == 409 || (response != null && "user_exists".equalsIgnoreCase(response.error()));
    }

    @Override
    public AppException translate(KeycloakErrorResponse response) {
        return new AppException(
                HttpStatus.CONFLICT,
                "USER_ALREADY_EXISTS",
                "Đăng ký thất bại: Tên tài khoản hoặc Email này đã tồn tại trên hệ thống."
        );
    }
}
