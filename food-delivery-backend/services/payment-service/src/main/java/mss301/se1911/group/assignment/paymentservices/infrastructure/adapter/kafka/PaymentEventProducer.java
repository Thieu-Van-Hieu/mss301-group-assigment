package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonevents.PaymentProcessedKafkaEvent;
import mss301.se1911.group.assignment.commonevents.PaymentFailedKafkaEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String PAYMENT_EVENTS_TOPIC = "payment-events-topic";

    public void publishPaymentProcessedEvent(PaymentProcessedKafkaEvent event) {
        log.info("Publishing PaymentProcessedKafkaEvent for order {}: {}", event.orderId(), event);
        kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, event.orderId().toString(), event);
    }

    public void publishPaymentFailedEvent(PaymentFailedKafkaEvent event) {
        log.info("Publishing PaymentFailedKafkaEvent for order {}: {}", event.orderId(), event);
        kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, event.orderId().toString(), event);
    }

}
