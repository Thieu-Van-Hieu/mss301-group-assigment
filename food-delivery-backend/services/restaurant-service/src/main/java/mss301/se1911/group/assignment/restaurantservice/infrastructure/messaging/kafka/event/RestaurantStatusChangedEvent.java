package mss301.se1911.group.assignment.restaurantservice.infrastructure.messaging.kafka.event;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record RestaurantStatusChangedEvent(
        UUID eventId,
        UUID restaurantId,
        String oldStatus,
        String newStatus,
        ZonedDateTime timestamp
) {}
