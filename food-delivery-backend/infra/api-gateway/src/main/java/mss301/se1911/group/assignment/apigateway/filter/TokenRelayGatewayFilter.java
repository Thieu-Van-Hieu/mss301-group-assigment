package mss301.se1911.group.assignment.apigateway.filter;

import mss301.se1911.group.assignment.apigateway.utils.KeycloakTokenParser;
import mss301.se1911.group.assignment.commonconstants.utils.GatewayConstraints;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Lớp bộ lọc toàn cục thực hiện chức năng xóa bỏ và tái cấu trúc các Header định danh từ JWT.
 * <p>
 * Bộ lọc này trích xuất thông tin người dùng từ mã cấu trúc bảo mật JWT trong hạ tầng Reactive Security Context,
 * mã hóa các thông tin chứa ký tự đặc biệt (tiếng Việt có dấu) theo chuẩn UTF-8, và đính kèm an toàn vào
 * HTTP Header trước khi định tuyến request đến các Microservice nội bộ.
 * </p>
 *
 * @author Thiều Văn Hiếu
 * @since 1.0.0
 */
@Component
public class TokenRelayGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest cleanedRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove(GatewayConstraints.HEADER_USER_ID);
                    httpHeaders.remove(GatewayConstraints.HEADER_EMAIL);
                    httpHeaders.remove(GatewayConstraints.HEADER_ROLE);
                    httpHeaders.remove(GatewayConstraints.HEADER_FULL_NAME);
                    httpHeaders.remove(GatewayConstraints.HEADER_PHONE);
                    httpHeaders.remove(GatewayConstraints.HEADER_ACTIVE);
                    httpHeaders.remove(GatewayConstraints.HEADER_ENABLED);
                })
                .build();

        ServerWebExchange cleanedExchange = exchange.mutate().request(cleanedRequest).build();

        return ReactiveSecurityContextHolder.getContext()
                .filter(securityContext -> securityContext.getAuthentication() != null && securityContext.getAuthentication().isAuthenticated())
                .map(securityContext -> securityContext.getAuthentication().getPrincipal())
                .filter(principal -> principal instanceof Jwt)
                .cast(Jwt.class)
                .map(KeycloakTokenParser::parse)
                .map(user -> {
                    String encodedFullName = user.fullName() != null
                            ? URLEncoder.encode(user.fullName(), StandardCharsets.UTF_8)
                            : "";

                    ServerHttpRequest authorizedRequest = cleanedExchange.getRequest().mutate()
                            .header(GatewayConstraints.HEADER_USER_ID, user.id() != null ? user.id() : "")
                            .header(GatewayConstraints.HEADER_EMAIL, user.email() != null ? user.email() : "")
                            .header(GatewayConstraints.HEADER_FULL_NAME, encodedFullName)
                            .header(GatewayConstraints.HEADER_PHONE, user.phoneNumber() != null ? user.phoneNumber() : "")
                            .header(GatewayConstraints.HEADER_ACTIVE, String.valueOf(user.active()))
                            .header(GatewayConstraints.HEADER_ENABLED, String.valueOf(user.enabled()))
                            .header(GatewayConstraints.HEADER_ROLE, user.realmRoles() != null ? String.join(",", user.realmRoles()) : "")
                            .build();

                    return cleanedExchange.mutate().request(authorizedRequest).build();
                })
                .defaultIfEmpty(cleanedExchange)
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return GatewayConstraints.ORDER_AUTHORIZATION_ID;
    }
}