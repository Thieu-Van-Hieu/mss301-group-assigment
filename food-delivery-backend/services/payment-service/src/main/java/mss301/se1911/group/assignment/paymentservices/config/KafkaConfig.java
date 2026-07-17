package mss301.se1911.group.assignment.paymentservices.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.payment-order-paid}")
    private String orderPaidTopic;

    @Value("${kafka.topics.payment-failed}")
    private String paymentFailedTopic;

    @Value("${kafka.topics.payment-payout-completed}")
    private String payoutCompletedTopic;

    @Bean
    public NewTopic orderPaidTopic() {
        return TopicBuilder.name(orderPaidTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name(paymentFailedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic payoutCompletedTopic() {
        return TopicBuilder.name(payoutCompletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Error handler with Dead Letter Topic (DLT) support.
     * - Retries 3 times with 1-second interval (handles out-of-order events / transient failures).
     * - After exhaustion, publishes the failed record to a DLT topic ({original-topic}.DLT)
     *   instead of silently dropping it (prevents Poison Pill from blocking the partition).
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
    }
}

