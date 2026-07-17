package mss301.se1911.group.assignment.customerservice.infrastructure.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.customerservice.application.command.RecordOrderCommand;
import mss301.se1911.group.assignment.customerservice.application.usecase.OrderHistoryProjectionUseCases;
import mss301.se1911.group.assignment.customerservice.infrastructure.messaging.kafka.event.OrderCompletedEvent;
import mss301.se1911.group.assignment.customerservice.infrastructure.messaging.kafka.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Nhận event từ Order Service để dựng read model lịch sử đơn hàng.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderHistoryProjectionUseCases orderHistoryProjectionUseCases;

    @KafkaListener(topics = "${app.kafka.topics.order-created}", groupId = "customer-service-order-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Nhận event OrderCreated: orderId={}, customerId={}", event.orderId(), event.customerId());

        try {
            RecordOrderCommand command = new RecordOrderCommand(
                    event.orderId(),
                    event.customerId(),
                    event.restaurantId(),
                    event.totalAmount(),
                    event.currency(),
                    event.status(),
                    event.itemsSummary(),
                    event.createdAt()
            );
            orderHistoryProjectionUseCases.recordCreated(command);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý event OrderCreated cho đơn {}: {}", event.orderId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.order-completed}", groupId = "customer-service-order-group")
    public void onOrderCompleted(OrderCompletedEvent event) {
        log.info("Nhận event OrderCompleted: orderId={}", event.orderId());

        try {
            orderHistoryProjectionUseCases.markCompleted(event.orderId(), event.completedAt());
        } catch (Exception e) {
            log.error("Lỗi khi xử lý event OrderCompleted cho đơn {}: {}", event.orderId(), e.getMessage(), e);
        }
    }
}
