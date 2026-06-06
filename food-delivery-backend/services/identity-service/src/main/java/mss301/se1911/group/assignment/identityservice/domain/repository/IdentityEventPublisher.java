package mss301.se1911.group.assignment.identityservice.domain.repository;

import mss301.se1911.group.assignment.identityservice.domain.event.UserCreatedEvent;

public interface IdentityEventPublisher {
    void publishUserCreated(UserCreatedEvent event);
}