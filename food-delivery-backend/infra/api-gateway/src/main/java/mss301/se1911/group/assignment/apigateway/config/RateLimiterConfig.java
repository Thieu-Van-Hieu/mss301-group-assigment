package mss301.se1911.group.assignment.apigateway.config;

import mss301.se1911.group.assignment.apigateway.utils.KeycloakTokenParser;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> ReactiveSecurityContextHolder.getContext()
                // 1. Kiểm tra xem request này đã được xác thực thành công qua Keycloak chưa
                .filter(securityContext -> securityContext.getAuthentication() != null
                        && securityContext.getAuthentication().isAuthenticated())
                .map(securityContext -> securityContext.getAuthentication().getPrincipal())
                // 2. Ép kiểu sang Jwt tương tự như Filter TokenRelay của bạn
                .filter(principal -> principal instanceof Jwt)
                .cast(Jwt.class)
                .map(KeycloakTokenParser::parse)
                // 3. Nếu là User hợp lệ -> Trả về User ID duy nhất để làm Key đếm request
                .map(user -> user.id() != null ? user.id() : "anonymous")

                // 4. Xử lý kịch bản TRANG PUBLIC (Khách vãng lai, không có Token / SecurityContext trống)
                .defaultIfEmpty("anonymous")
                .flatMap(key -> {
                    if ("anonymous".equals(key)) {
                        // Lấy IP của máy khách làm Key thay thế
                        if (exchange.getRequest().getRemoteAddress() != null) {
                            return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
                        }
                        return Mono.just("anonymous-ip");
                    }
                    return Mono.just(key);
                });
    }
}