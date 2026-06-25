package mss301.se1911.group.assignment.apigateway.utils;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record KeycloakUser(
        String id,
        String email,
        String fullName,
        String phoneNumber,
        boolean active,
        boolean enabled,
        List<String> realmRoles,
        Map<String, List<String>> clientRoles
) {
    public boolean hasRealmRole(String role) {
        return realmRoles != null && realmRoles.contains(role);
    }
}