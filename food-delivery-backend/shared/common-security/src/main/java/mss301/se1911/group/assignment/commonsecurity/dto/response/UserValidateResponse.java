package mss301.se1911.group.assignment.commonsecurity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidateResponse {

    private boolean active;

    @JsonProperty("sub")
    private String id;

    private String email;

    @JsonProperty("realm_access")
    private RealmAccess realmAccess;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    private List<String> roles;
    private boolean enabled;

    // Sử dụng Object để chấp nhận cả chuỗi đơn lẫn mảng (đề phòng Keycloak đổi cấu hình)
    @JsonSetter("fullName")
    public void setFullNameFromKeycloak(Object fullNameObj) {
        if (fullNameObj instanceof List<?> list) {
            if (!list.isEmpty()) {
                this.fullName = String.valueOf(list.getFirst());
            }
        } else if (fullNameObj != null) {
            this.fullName = String.valueOf(fullNameObj);
        }
    }

    @JsonSetter("phoneNumber")
    public void setPhoneNumberFromKeycloak(Object phoneNumberObj) {
        if (phoneNumberObj instanceof List<?> list) {
            if (!list.isEmpty()) {
                this.phoneNumber = String.valueOf(list.getFirst());
            }
        } else if (phoneNumberObj != null) {
            this.phoneNumber = String.valueOf(phoneNumberObj);
        }
    }

    public void flattenData() {
        if (this.realmAccess != null) {
            this.roles = this.realmAccess.getRoles();
        }
        // Gán luôn trạng thái enabled dựa vào trường active từ token Keycloak
        this.enabled = this.active;
    }

    @Data
    public static class RealmAccess {
        private List<String> roles;
    }
}