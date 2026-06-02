package mss301.se1911.group.assignment.apigateway.config;

import mss301.se1911.group.assignment.commonsecurity.utils.KeycloakRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity // Kích hoạt tính năng bảo mật cho môi trường Reactive (WebFlux) của Gateway
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Tạm thời tắt CSRF khi chạy local, sẽ bật lại sau
                .authorizeExchange(exchange -> exchange
                        // Luồng đăng ký tài khoản mới: Bắt buộc phải MỞ (permitAll) để khách hàng vãng lai bấm đăng ký được
                        .pathMatchers("/api/v1/customers/register").permitAll()

                        // Mọi API liên quan đến xem/sửa cấu hình hệ thống (Actuator) cũng mở cho Prometheus check tải
                        .pathMatchers("/actuator/**").permitAll()

                        // Các API còn lại (Đặt hàng, giao hàng, ví tiền...) bắt buộc phải có Token hợp lệ mới cho qua
                        .anyExchange().authenticated()
                )
                // Khai báo cho Gateway biết nó đóng vai trò là một Resource Server chặn JWT Token
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                );

        return http.build();
    }

    /**
     * Hàm cấu hình sử dụng lớp chuyển đổi KeycloakRoleConverter từ module shared
     * để giúp Spring Security tại Gateway hiểu và map được các Role từ Keycloak.
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // Nhúng class tiện ích từ module Shared vào đây
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
