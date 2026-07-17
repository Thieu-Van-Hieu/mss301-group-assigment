package mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonevents.OrderCompletedKafkaEvent;
import mss301.se1911.group.assignment.paymentservices.application.command.ProcessPayoutCommand;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payout.ProcessOrderPayoutUseCase;
import mss301.se1911.group.assignment.paymentservices.application.command.ConfirmCodPaymentCommand;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.ConfirmCodPaymentUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payment.GetPaymentByOrderIdUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentMethod;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompletedEventConsumer {

    private final ProcessOrderPayoutUseCase processOrderPayoutUseCase;
    private final GetPaymentByOrderIdUseCase getPaymentByOrderIdUseCase;
    private final ConfirmCodPaymentUseCase confirmCodPaymentUseCase;

    @KafkaListener(topics = "order-events-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Object message) {
        if (message instanceof OrderCompletedKafkaEvent event) {
            log.info("Received OrderCompletedKafkaEvent for order {}. Checking payment and processing payout...", event.orderId());
            try {
                PaymentTransaction transaction = getPaymentByOrderIdUseCase.execute(event.orderId());
                if (transaction.getPaymentMethod() == PaymentMethod.COD && transaction.getStatus() != PaymentStatus.PAID) {
                    log.info("Auto-confirming COD payment for order {}", event.orderId());
                    confirmCodPaymentUseCase.execute(new ConfirmCodPaymentCommand(event.orderId()));
                }

                ProcessPayoutCommand command = new ProcessPayoutCommand(event);
                processOrderPayoutUseCase.execute(command);
                log.info("Successfully processed payout for order {}", event.orderId());
            } catch (Exception e) {
                log.error("Failed to process payout for order {}", event.orderId(), e);
                throw e; // Rely on Kafka retry mechanism
            }
        }
    }
}
