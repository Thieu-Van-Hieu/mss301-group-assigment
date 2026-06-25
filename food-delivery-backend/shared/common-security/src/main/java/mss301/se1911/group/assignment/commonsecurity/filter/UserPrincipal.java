package mss301.se1911.group.assignment.commonsecurity.filter;

import java.util.List;

public record UserPrincipal(
        String id,
        String email,
        String fullName,
        String phoneNumber,
        boolean active,
        boolean enabled,
        List<String> roles
) {
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}
