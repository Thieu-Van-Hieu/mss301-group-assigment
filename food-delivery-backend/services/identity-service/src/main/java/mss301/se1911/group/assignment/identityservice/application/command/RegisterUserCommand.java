package mss301.se1911.group.assignment.identityservice.application.command;

public record RegisterUserCommand(
        String fullName,
        String phoneNumber,
        String email,
        String password,
        String role
) {
}
