package mss301.se1911.group.assignment.identityservice.api.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KeycloakErrorResponse(
        String error,
        String errorDescription
) {
}
