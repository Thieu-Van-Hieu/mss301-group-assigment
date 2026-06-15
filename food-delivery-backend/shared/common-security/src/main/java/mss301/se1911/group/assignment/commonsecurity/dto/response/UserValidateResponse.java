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

    @JsonSetter("fullName")
    public void setFullNameFromKeycloak(List<String> fullNameList) {
        if (fullNameList != null && !fullNameList.isEmpty()) {
            this.fullName = fullNameList.getFirst();
        }
    }

    @JsonSetter("phoneNumber")
    public void setPhoneNumberFromKeycloak(List<String> phoneNumberList) {
        if (phoneNumberList != null && !phoneNumberList.isEmpty()) {
            this.phoneNumber = phoneNumberList.getFirst();
        }
    }

    public void flattenData() {
        if (this.realmAccess != null) {
            this.roles = this.realmAccess.getRoles();
        }
    }

    @Data
    public static class RealmAccess {
        private List<String> roles;
    }
}