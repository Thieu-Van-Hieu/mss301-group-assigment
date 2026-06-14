package mss301.se1911.group.assignment.identityservice.infrastructure.adapter;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonclient.exceptioin.ErrorTranslator;
import mss301.se1911.group.assignment.commonclient.translator.ErrorTranslationContext;
import mss301.se1911.group.assignment.commonclient.translator.ErrorTranslationExecutor;
import mss301.se1911.group.assignment.identityservice.api.dto.response.KeycloakErrorResponse;
import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityRepository;
import mss301.se1911.group.assignment.identityservice.infrastructure.mapper.KeycloakAccountMapper;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakIdentityAdapter implements IdentityRepository {

    private final Keycloak keycloak;
    private final KeycloakAccountMapper accountMapper;
    private final List<ErrorTranslator<KeycloakErrorResponse>> errorTranslators;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public String create(Account account, String rawPassword) {
        UserRepresentation user = accountMapper.toUserRepresentation(account, rawPassword);
        UsersResource usersResource = keycloak.realm(realm).users();

        try (Response response = usersResource.create(user)) {
            final var status = response.getStatus();
            if (response.getStatus() != 201) {
                ErrorTranslationContext<KeycloakErrorResponse> context =
                        ErrorTranslationContext.<KeycloakErrorResponse>builder()
                                .translators(errorTranslators)
                                .status(status)
                                .responseBody(null)
                                .fallbackStatus(HttpStatus.BAD_GATEWAY)
                                .fallbackErrorCode("IDENTITY_CREATION_FAILED")
                                .fallbackMessage("Không thể đăng ký tài khoản trên hệ thống định danh Keycloak.")
                                .onFallbackTriggered(rawBody -> {
                                    log.error("[CRITICAL WARNING] Hệ thống đang bị THIẾU BỘ DỊCH LỖI (ErrorTranslator)!");
                                    log.error("-> HTTP Status nhận về từ bên ngoài: {}", status);
                                    if (rawBody != null) {
                                        log.error("-> Dữ liệu JSON lỗi thô chưa được dịch: Error='{}', Description='{}'",
                                                rawBody.error(), rawBody.errorDescription());
                                    }
                                    log.error("-> Vui lòng tạo thêm một class @Component triển khai ErrorTranslator để xử lý mã lỗi này.");
                                })
                                .build();

                ErrorTranslationExecutor.executeAndThrow(context);
            }

            String userId = CreatedResponseUtil.getCreatedId(response);

            try {
                UserResource userResource = usersResource.get(userId);
                RoleRepresentation realmRole = keycloak.realm(realm).roles().get(account.getRole()).toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(realmRole));
            } catch (Exception e) {
                usersResource.delete(userId);
                throw new RuntimeException("Lỗi phân quyền hệ thống: " + e.getMessage());
            }

            return userId;

        }
    }
}