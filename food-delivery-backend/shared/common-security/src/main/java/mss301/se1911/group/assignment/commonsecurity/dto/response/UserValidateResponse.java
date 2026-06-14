package mss301.se1911.group.assignment.commonsecurity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private List<String> fullNameList;

    @JsonProperty("phoneNumber")
    private List<String> phoneNumberList;

    private String fullName;
    private String phoneNumber;
    private List<String> roles;
    private boolean enabled;

    public void flattenData() {
        if (this.realmAccess != null) {
            this.roles = this.realmAccess.getRoles();
        }
        if (this.fullNameList != null && !this.fullNameList.isEmpty()) {
            this.fullName = this.fullNameList.getFirst();
        }
        if (this.phoneNumberList != null && !this.phoneNumberList.isEmpty()) {
            this.phoneNumber = this.phoneNumberList.getFirst();
        }
    }

    @Data
    public static class RealmAccess {
        private List<String> roles;
    }
}

