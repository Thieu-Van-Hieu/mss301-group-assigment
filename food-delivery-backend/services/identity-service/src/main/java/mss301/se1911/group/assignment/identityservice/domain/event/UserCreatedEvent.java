package mss301.se1911.group.assignment.identityservice.domain.event;

public record UserCreatedEvent(
        String userId,
        String username,
        String email,
        String role
) {
}