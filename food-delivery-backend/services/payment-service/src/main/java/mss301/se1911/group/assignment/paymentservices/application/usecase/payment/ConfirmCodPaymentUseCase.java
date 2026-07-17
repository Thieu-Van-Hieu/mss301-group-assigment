package mss301.se1911.group.assignment.paymentservices.application.usecase.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonevents.PaymentProcessedKafkaEvent;
import mss301.se1911.group.assignment.paymentservices.application.command.ConfirmCodPaymentCommand;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.kafka.PaymentEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmCodPaymentUseCase {

    private final GetPaymentByOrderIdUseCase getPaymentByOrderIdUseCase;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public PaymentTransaction execute(ConfirmCodPaymentCommand command) {
        PaymentTransaction transaction = getPaymentByOrderIdUseCase.execute(command.orderId());
        PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.from(transaction);

        if (aggregate.isPaid()) {
            return aggregate.getTransaction(); // Idempotent
        }

        aggregate.confirmCodPayment();
        PaymentTransaction saved = paymentTransactionRepository.save(aggregate.getTransaction());

        publishOrderPaidEvent(saved);
        return saved;
    }

    private void publishOrderPaidEvent(PaymentTransaction transaction) {
        PaymentProcessedKafkaEvent event = new PaymentProcessedKafkaEvent(
                transaction.getOrderId(),
                "TXN-" + transaction.getId().toString().substring(0, 8),
                transaction.getMoney().getAmount(),
                transaction.getMoney().getCurrency(),
                "SUCCESS"
        );
        paymentEventProducer.publishPaymentProcessedEvent(event);
    }
}
