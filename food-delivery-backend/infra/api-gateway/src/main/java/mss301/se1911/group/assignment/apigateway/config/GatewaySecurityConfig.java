package mss301.se1911.group.assignment.apigateway.config;

import jakarta.ws.rs.HttpMethod;
import mss301.se1911.group.assignment.apigateway.utils.KeycloakRoleConverter;
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
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Tắt CSRF
                .authorizeExchange(exchange -> exchange
                        // 1. Cho phép tất cả các request OPTIONS (CORS Preflight) đi qua
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Mở cổng công khai cho toàn bộ các endpoint Auth (Register, Exchange Code, Refresh...)
                        .pathMatchers("/api/v1/auth/**").permitAll()

                        // 3. Mở cổng cho hệ thống giám sát Actuator
                        .pathMatchers("/actuator/**").permitAll()

                        // 4. Sửa lại đường dẫn Swagger UI chuẩn chỉ (Có dấu gạch chéo đầu đường dẫn)
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/auth/v3/api-docs/**"
                        ).permitAll()

                        // 5. TẤT CẢ CÁC API NGHIỆP VỤ CÒN LẠI (Order, Product...) BẮT BUỘC PHẢI LOGIN
                        .anyExchange().authenticated()
                )
                // Cấu hình Resource Server giải mã JWT bằng Converter Reactive thuần chủng
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
