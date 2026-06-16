package mss301.se1911.group.assignment.identityservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.identityservice.api.dto.response.LoginUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetLoginUrlUseCase {
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${app.keycloak.frontend-redirect-uri}")
    private String frontendRedirectUri;

    @Value("${app.keycloak.redirect-server-url}")
    private String redirectServerUrl;

    public LoginUrlResponse execute() {
        String state = UUID.randomUUID().toString();

        // 1. Logic mở rộng: Lưu 'state' vào Redis để kích hoạt bảo mật chống CSRF
        // redisTemplate.opsForValue().set("auth:state:" + state, "pending", Duration.ofMinutes(5));

        // 2. Xây dựng chuỗi URL
        String targetUrl = UriComponentsBuilder.fromUriString(redirectServerUrl)
                .path("/realms/{realm}/protocol/openid-connect/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", frontendRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid")
                .queryParam("state", state)
                .buildAndExpand(realm)
                .toUriString();

        return new LoginUrlResponse(targetUrl, state);
    }
}
