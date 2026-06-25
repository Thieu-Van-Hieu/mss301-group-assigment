package mss301.se1911.group.assignment.apigateway.utils;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeycloakTokenParser {

    public static KeycloakUser parse(Jwt jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("JWT token cannot be null");
        }

        Map<String, Object> claims = jwt.getClaims();

        // 1. Định danh & trạng thái cơ bản
        String id = jwt.getSubject(); // Lấy từ claim "sub"
        String email = jwt.getClaimAsString("email");
        boolean active = !claims.containsKey("active") || Boolean.TRUE.equals(claims.get("active"));
        boolean enabled = !claims.containsKey("enabled") || Boolean.TRUE.equals(claims.get("enabled"));

        // 2. Xử lý Custom Attributes (Lấy phần tử đầu tiên từ List giống DTO của bạn)
        String fullName = extractFirstElement(claims.get("fullName"));
        // Nếu trường tiêu chuẩn "name" có dữ liệu mà fullName trống, có thể fallback về name
        if (fullName == null || fullName.isEmpty()) {
            fullName = jwt.getClaimAsString("name");
        }

        String phoneNumber = extractFirstElement(claims.get("phoneNumber"));

        // 3. Trích xuất Realm Roles
        List<String> realmRoles = Collections.emptyList();
        if (claims.get("realm_access") instanceof Map<?, ?> realmAccess &&
                realmAccess.get("roles") instanceof List<?> roles) {
            realmRoles = roles.stream()
                    .filter(String.class::isInstance)
                    .map(Object::toString)
                    .toList();
        }

        // 4. Trích xuất Client Roles
        Map<String, List<String>> clientRoles = new HashMap<>();
        if (claims.get("resource_access") instanceof Map<?, ?> resourceAccess) {
            resourceAccess.forEach((clientKey, clientConfig) -> {
                if (clientKey instanceof String clientId && clientConfig instanceof Map<?, ?> configMap) {
                    if (configMap.get("roles") instanceof List<?> roles) {
                        List<String> extractedRoles = roles.stream()
                                .filter(String.class::isInstance)
                                .map(Object::toString)
                                .toList();
                        clientRoles.put(clientId, extractedRoles);
                    }
                }
            });
        }

        return new KeycloakUser(
                id, email, fullName, phoneNumber, active, enabled,
                realmRoles, Collections.unmodifiableMap(clientRoles)
        );
    }

    // Hàm bổ trợ bóc tách phần tử đầu tiên từ Object (hỗ trợ cả chuỗi đơn lẫn mảng chuỗi)
    private static String extractFirstElement(Object obj) {
        if (obj instanceof List<?> list && !list.isEmpty()) {
            Object first = list.getFirst();
            return first != null ? first.toString() : null;
        } else if (obj instanceof String str) {
            return str;
        }
        return null;
    }
}