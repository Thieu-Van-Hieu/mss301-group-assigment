package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonevents.PaymentFailedKafkaEvent;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentStatus;
import mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.kafka.PaymentEventProducer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentTimeoutScheduler {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentEventProducer paymentEventProducer;

    /**
     * Run every 5 minutes to check for pending or processing payments that have timed out.
     * Timeout is set to 20 minutes from creation.
     */
    @Scheduled(fixedRateString = "${payment.timeout.check-interval:300000}")
    @Transactional
    public void checkTimeoutPayments() {
        log.info("Running scheduled check for timeout payments");

        OffsetDateTime timeoutThreshold = OffsetDateTime.now().minusMinutes(20);

        // Find PENDING payments
        List<PaymentTransaction> pendingTransactions = paymentTransactionRepository
                .findByStatusAndCreatedAtBefore(PaymentStatus.PENDING, timeoutThreshold);

        // Find PROCESSING payments
        List<PaymentTransaction> processingTransactions = paymentTransactionRepository
                .findByStatusAndCreatedAtBefore(PaymentStatus.PROCESSING, timeoutThreshold);

        processTimeouts(pendingTransactions, "Timeout while in PENDING status");
        processTimeouts(processingTransactions, "Timeout while in PROCESSING status");
    }

    private void processTimeouts(List<PaymentTransaction> transactions, String reason) {
        for (PaymentTransaction tx : transactions) {
            log.info("Marking transaction {} for order {} as FAILED due to timeout",
                    tx.getId(), tx.getOrderId());

            PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.from(tx);
            aggregate.markFailed(reason);
            paymentTransactionRepository.save(aggregate.getTransaction());

            // Publish PaymentFailedKafkaEvent to Kafka
            PaymentFailedKafkaEvent event = new PaymentFailedKafkaEvent(
                    tx.getOrderId(),
                    tx.getMoney().getAmount(),
                    reason
            );
            paymentEventProducer.publishPaymentFailedEvent(event);
        }
    }
}
