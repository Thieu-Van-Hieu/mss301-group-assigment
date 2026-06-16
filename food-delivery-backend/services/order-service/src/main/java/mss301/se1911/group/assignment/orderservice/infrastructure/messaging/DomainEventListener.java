package mss301.se1911.group.assignment.orderservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.*;
import mss301.se1911.group.assignment.commonevents.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("🔥 Domain Event Listener: Order {} created. Emit to customer, restaurant boundary. Total: {} {}", 
                event.getOrder().getId(), 
                event.getOrder().getTotalAmount().getAmount(),
                event.getOrder().getTotalAmount().getCurrency());
        
        OrderCreatedKafkaEvent kafkaEvent = new OrderCreatedKafkaEvent(
                event.getOrder().getId(),
                event.getOrder().getCustomerId(),
                event.getOrder().getTotalAmount().getAmount(),
                event.getOrder().getTotalAmount().getCurrency(),
                event.getOrder().getDeliveryInfo() != null ? event.getOrder().getDeliveryInfo().getAddress() : null,
                event.getOrder().getDeliveryInfo() != null ? event.getOrder().getDeliveryInfo().getPhone() : null
        );
        kafkaTemplate.send("order-events-topic", event.getOrder().getId().toString(), kafkaEvent);
        log.info("Sent OrderCreatedKafkaEvent to Kafka for Order ID: {}", event.getOrder().getId());
    }

    @EventListener
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("🔥 Domain Event Listener: Order {} is PAID. Emit to delivery and restaurant boundaries. TransactionId: {}", 
                event.getOrder().getId(),
                event.getOrder().getPaymentInfo().getTransactionId());
        
        OrderPaidKafkaEvent kafkaEvent = new OrderPaidKafkaEvent(
                event.getOrder().getId(),
                event.getOrder().getCustomerId(),
                event.getOrder().getTotalAmount().getAmount(),
                event.getOrder().getTotalAmount().getCurrency(),
                event.getOrder().getDeliveryInfo() != null ? event.getOrder().getDeliveryInfo().getAddress() : null,
                event.getOrder().getDeliveryInfo() != null ? event.getOrder().getDeliveryInfo().getPhone() : null
        );
        kafkaTemplate.send("order-events-topic", event.getOrder().getId().toString(), kafkaEvent);
        log.info("Sent OrderPaidKafkaEvent to Kafka for Order ID: {}", event.getOrder().getId());
    }

    @EventListener
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("🔥 Domain Event Listener: Order {} is CONFIRMED. Emit to delivery service.", 
                event.getOrder().getId());
    }

    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("🔥 Domain Event Listener: Order {} is CANCELLED. Reason: {}. Emit refund and restore inventory.", 
                event.getOrder().getId(), 
                event.getReason());
        
        OrderCancelledKafkaEvent kafkaEvent = new OrderCancelledKafkaEvent(
                event.getOrder().getId(),
                event.getReason()
        );
        kafkaTemplate.send("order-events-topic", event.getOrder().getId().toString(), kafkaEvent);
        log.info("Sent OrderCancelledKafkaEvent to Kafka for Order ID: {}", event.getOrder().getId());
    }
}
