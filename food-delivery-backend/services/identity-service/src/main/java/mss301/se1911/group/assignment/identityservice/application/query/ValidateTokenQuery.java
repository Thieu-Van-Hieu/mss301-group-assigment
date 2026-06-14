package mss301.se1911.group.assignment.identityservice.application.query;

import lombok.Builder;

@Builder
public record ValidateTokenQuery(String accessToken) {
}
