package mss301.se1911.group.assignment.commonsecurity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record TokenResponse(
        @JsonProperty(value = "access_token", access = JsonProperty.Access.WRITE_ONLY)
        @Schema(name = "accessToken", accessMode = Schema.AccessMode.READ_ONLY)
        String accessToken,

        @JsonProperty(value = "refresh_token", access = JsonProperty.Access.WRITE_ONLY)
        @Schema(hidden = true)
        String refreshToken,

        @JsonProperty(value = "expires_in", access = JsonProperty.Access.WRITE_ONLY)
        @Schema(name = "expiresIn", accessMode = Schema.AccessMode.READ_ONLY)
        long expiresIn,

        @Schema(hidden = true)
        @JsonProperty(value = "refresh_expires_in", access = JsonProperty.Access.WRITE_ONLY)
        long refreshExpiresIn
) {
}
