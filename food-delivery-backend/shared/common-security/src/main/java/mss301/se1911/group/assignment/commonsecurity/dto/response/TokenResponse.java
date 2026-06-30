package mss301.se1911.group.assignment.commonsecurity.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TokenResponse(
        @JsonProperty(value = "accessToken")
        @JsonAlias("access_token")
        String accessToken,

        @JsonProperty(value = "refreshToken")
        @JsonAlias("refresh_token")
        String refreshToken,

        @JsonProperty(value = "expiresIn")
        @JsonAlias("expires_in")
        long expiresIn,

        @JsonProperty(value = "refreshExpiresIn")
        @JsonAlias("refresh_expires_in")
        long refreshExpiresIn
) {
}
