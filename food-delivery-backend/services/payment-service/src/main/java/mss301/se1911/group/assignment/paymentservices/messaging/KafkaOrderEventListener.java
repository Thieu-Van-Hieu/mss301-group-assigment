package mss301.se1911.group.assignment.paymentservices.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.paymentservices.domain.PaymentTransaction;
import mss301.se1911.group.assignment.commonevents.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // In-memory store for payment transactions
    private final Map<UUID, PaymentTransaction> transactionStore = new ConcurrentHashMap<>();

    @KafkaListener(topics = "order-events-topic", groupId = "payment-service-group")
    public void handleOrderEvents(Object message) {
        log.info("Received event in payment-service: {}", message);
        try {
            if (message instanceof OrderCreatedKafkaEvent event) {
                log.info("Processing payment for Order: {}, Amount: {} {}", event.orderId(), event.amount(), event.currency());
                
                PaymentTransaction transaction = new PaymentTransaction(
                        UUID.randomUUID(),
                        event.orderId(),
                        event.amount(),
                        event.currency(),
                        "PENDING"
                );
                
                // Simulating failure: amount > 1000
                if (event.amount().compareTo(new BigDecimal("1000")) > 0) {
                    log.warn("Payment failed due to amount limit exceed (> 1000): {}", event.amount());
                    transaction.setStatus("FAILED");
                    transactionStore.put(event.orderId(), transaction);
                    
                    PaymentFailedKafkaEvent failEvent = new PaymentFailedKafkaEvent(
                            event.orderId(),
                            event.amount(),
                            "Insufficient balance (simulated amount > 1000)"
                    );
                    kafkaTemplate.send("payment-events-topic", event.orderId().toString(), failEvent);
                    log.info("Published PaymentFailedKafkaEvent to Kafka.");
                } else {
                    log.info("Payment processed successfully.");
                    transaction.setStatus("SUCCESS");
                    transactionStore.put(event.orderId(), transaction);
                    
                    PaymentProcessedKafkaEvent successEvent = new PaymentProcessedKafkaEvent(
                            event.orderId(),
                            "TXN-" + transaction.getId().toString().substring(0, 8),
                            event.amount(),
                            event.currency(),
                            "SUCCESS"
                    );
                    kafkaTemplate.send("payment-events-topic", event.orderId().toString(), successEvent);
                    log.info("Published PaymentProcessedKafkaEvent to Kafka.");
                }
            } else if (message instanceof OrderCancelledKafkaEvent event) {
                log.info("Compensating Transaction: Refunding payment for Order: {}, Reason: {}", event.orderId(), event.reason());
                PaymentTransaction transaction = transactionStore.get(event.orderId());
                if (transaction != null) {
                    if ("SUCCESS".equals(transaction.getStatus())) {
                        transaction.setStatus("REFUNDED");
                        transactionStore.put(event.orderId(), transaction);
                        log.info("Payment for Order {} has been REFUNDED successfully.", event.orderId());
                    } else {
                        log.info("Transaction status is {}, no refund required.", transaction.getStatus());
                    }
                } else {
                    log.warn("No transaction found for Order: {}, nothing to refund.", event.orderId());
                }
            }
        } catch (Exception e) {
            log.error("Error handling order event: {}", e.getMessage(), e);
        }
    }
}
