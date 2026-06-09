package mss301.se1911.group.assignment.commonevents.identity;

import lombok.Builder;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
public record UserCreatedEvent(
        UUID eventId,
        Timestamp timestamp,
        String userId,
        String fullName,
        String email,
        String phoneNumber,
        String username,
        String role
) {
}