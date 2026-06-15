package mss301.se1911.group.assignment.identityservice.infrastructure.mapper;

import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface KeycloakAccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "account.email") // Dùng email làm username đăng nhập luôn
    @Mapping(target = "email", source = "account.email")
    @Mapping(target = "enabled", source = "account.enabled")
    @Mapping(target = "firstName", ignore = true) // Sẽ xử lý ở hàm @AfterMapping
    @Mapping(target = "lastName", ignore = true)  // Sẽ xử lý ở hàm @AfterMapping
    @Mapping(target = "attributes", ignore = true)// Sẽ xử lý ở hàm @AfterMapping
    @Mapping(target = "emailVerified", constant = "true")
    @Mapping(target = "credentials", source = "rawPassword", qualifiedByName = "toCredentials")
    UserRepresentation toUserRepresentation(Account account, String rawPassword);

    @Named("toCredentials")
    default List<CredentialRepresentation> toCredentials(String rawPassword) {
        if (rawPassword == null) return null;

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(rawPassword);

        return Collections.singletonList(credential);
    }

    /**
     * Hàm này chạy NGAY SAU KHI MapStruct thực hiện xong các bước map cơ bản phía trên.
     * Dùng để xử lý các logic phức tạp như tách chuỗi fullName và nhồi dữ liệu vào Attributes.
     */
    @AfterMapping
    default void handleCustomFields(Account account, @MappingTarget UserRepresentation target) {
        // 1. Khởi tạo Map attributes (Đảm bảo không bị đè hoặc NullPointerException)
        Map<String, List<String>> attributes = target.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<>();
        }

        // 2. Đẩy fullName và phoneNumber vào Attributes
        if (account.getFullName() != null && !account.getFullName().isBlank()) {
            attributes.put("fullName", Collections.singletonList(account.getFullName().trim()));
        }
        if (account.getPhoneNumber() != null && !account.getPhoneNumber().isBlank()) {
            attributes.put("phoneNumber", Collections.singletonList(account.getPhoneNumber().trim()));
        }
        target.setAttributes(attributes);

        // 3. Tách fullName thành firstName và lastName cho Keycloak Core
        if (account.getFullName() != null && !account.getFullName().isBlank()) {
            String fullTrimmed = account.getFullName().trim();
            int lastSpaceIndex = fullTrimmed.lastIndexOf(" ");

            if (lastSpaceIndex != -1) {
                // Ví dụ: "Nguyen Van A" -> LastName: "Nguyen Van", FirstName: "A"
                target.setLastName(fullTrimmed.substring(0, lastSpaceIndex));
                target.setFirstName(fullTrimmed.substring(lastSpaceIndex + 1));
            } else {
                // Trường hợp tên chỉ có đúng 1 từ
                target.setFirstName(fullTrimmed);
                target.setLastName("");
            }
        }
    }
}