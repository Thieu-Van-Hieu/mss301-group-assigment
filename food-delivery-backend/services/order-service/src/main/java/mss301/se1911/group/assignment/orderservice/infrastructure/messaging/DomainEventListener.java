package mss301.se1911.group.assignment.orderservice.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.orderservice.domain.event.*;

@Slf4j
@Component
public class DomainEventListener {

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("🔥 Domain Event Listener: Order {} created. Emit to customer, restaurant boundary. Total: {} {}", 
                event.getOrder().getId(), 
                event.getOrder().getTotalAmount().getAmount(),
                event.getOrder().getTotalAmount().getCurrency());
    }

    @EventListener
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("🔥 Domain Event Listener: Order {} is PAID. Emit to delivery and restaurant boundaries. TransactionId: {}", 
                event.getOrder().getId(),
                event.getOrder().getPaymentInfo().getTransactionId());
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
    }
}
