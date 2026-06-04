package mss301.se1911.group.assignment.identityservice.application.command;

public record RegisterUserCommand(
        String username,
        String password,
        String email,
        String role
) {
}
