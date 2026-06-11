package mss301.se1911.group.assignment.identityservice.infrastructure.adapter;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class KeycloakIdentityAdapter implements IdentityRepository {

    private final Keycloak keycloak;
    private final KeycloakAccountMapper accountMapper; // <--- TIÊM MAPPER VÀO ĐÂY

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public String create(Account account, String rawPassword) {
        UserRepresentation user = accountMapper.toUserRepresentation(account, rawPassword);
        UsersResource usersResource = keycloak.realm(realm).users();

        try (Response response = usersResource.create(user)) {

            if (response.getStatus() != 201) {
                throw new RuntimeException("Không thể tạo tài khoản trên Keycloak. Mã lỗi: " + response.getStatus());
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