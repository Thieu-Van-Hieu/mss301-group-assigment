package mss301.se1911.group.assignment.identityservice.api.dto;

public record UserRegisterRequest(
        String username,
        String password,
        String email,
        String role
) {
}
