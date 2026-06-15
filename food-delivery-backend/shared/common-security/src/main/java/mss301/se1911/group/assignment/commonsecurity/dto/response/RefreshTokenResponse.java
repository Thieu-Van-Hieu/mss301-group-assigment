package mss301.se1911.group.assignment.commonsecurity.dto.response;

import lombok.Builder;

@Builder
public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
