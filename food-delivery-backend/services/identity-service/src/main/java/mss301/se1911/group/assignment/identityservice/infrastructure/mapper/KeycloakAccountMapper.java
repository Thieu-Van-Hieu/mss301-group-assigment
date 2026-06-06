package mss301.se1911.group.assignment.identityservice.infrastructure.mapper;

import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface KeycloakAccountMapper {

    @Mapping(target = "id", ignore = true) // Cứ ignore ID vì Keycloak sẽ tự sinh UUID khi tạo mới
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "emailVerified", constant = "true") // Mặc định verify email luôn
    @Mapping(target = "credentials", source = "rawPassword", qualifiedByName = "toCredentials")
    UserRepresentation toUserRepresentation(Account account, String rawPassword);

    // Hàm phụ trợ (Custom mapping) để tự động đóng gói password thô thành cấu trúc Credential của Keycloak
    @Named("toCredentials")
    default List<CredentialRepresentation> toCredentials(String rawPassword) {
        if (rawPassword == null) return null;

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(rawPassword);

        return Collections.singletonList(credential);
    }
}