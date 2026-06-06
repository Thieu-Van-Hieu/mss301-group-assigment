package mss301.se1911.group.assignment.identityservice.domain.event;

import lombok.Builder;

@Builder
public record UserCreatedEvent(
        String userId,
        String fullName,
        String email,
        String phoneNumber,
        String username,
        String role
) {
}