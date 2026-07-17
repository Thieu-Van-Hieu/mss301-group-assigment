package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonevents.OrderCancelledKafkaEvent;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.ProcessRefundUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentTransactionNotFoundException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listens for OrderCancelledKafkaEvent and triggers refund processing.
 * If the order was paid via a third-party gateway, the amount is credited back to the customer's wallet.
 * If the order was COD, the transaction is simply marked as REFUNDED.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledEventConsumer {

    private final ProcessRefundUseCase processRefundUseCase;

    @KafkaListener(topics = "order-events-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Object message) {
        if (message instanceof OrderCancelledKafkaEvent event) {
            log.info("Received OrderCancelledKafkaEvent for order {}. Reason: {}", event.orderId(), event.reason());
            try {
                processRefundUseCase.execute(event.orderId());
                log.info("Successfully processed refund for cancelled order {}", event.orderId());
            } catch (PaymentTransactionNotFoundException e) {
                // No payment exists for this order (e.g., order cancelled before payment was initiated)
                log.warn("No payment found for cancelled order {}, skipping refund", event.orderId());
            } catch (PaymentProcessingException e) {
                // Payment exists but is not in a refundable state (e.g., PENDING, FAILED)
                log.warn("Cannot refund order {}: {}", event.orderId(), e.getMessage());
            } catch (Exception e) {
                log.error("Failed to process refund for cancelled order {}", event.orderId(), e);
                throw e; // Rely on Kafka retry mechanism
            }
        }
    }
}
