package mss301.se1911.group.assignment.identityservice.api.dto.request;

public record UserRegisterRequest(
        String fullName,
        String email,
        String phoneNumber,
        String password,
        String role
) {
}
