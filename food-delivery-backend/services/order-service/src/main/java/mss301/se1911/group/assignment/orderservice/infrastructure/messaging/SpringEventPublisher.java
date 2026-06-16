package mss301.se1911.group.assignment.orderservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.orderservice.application.usecase.OrderEventPublisher;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(OrderEvent event) {
        log.info("Publishing domain event: {}", event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }
}
