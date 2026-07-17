package mss301.se1911.group.assignment.restaurantservice.infrastructure.messaging.kafka.event;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record MenuUpdatedEvent(
        UUID eventId,
        UUID restaurantId,
        UUID menuItemId,
        String action, // CREATED, UPDATED, DELETED
        ZonedDateTime timestamp
) {}
