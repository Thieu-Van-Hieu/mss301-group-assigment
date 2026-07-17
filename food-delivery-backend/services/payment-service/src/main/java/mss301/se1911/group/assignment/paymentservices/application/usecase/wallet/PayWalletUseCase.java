package mss301.se1911.group.assignment.paymentservices.application.usecase.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonevents.PaymentProcessedKafkaEvent;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.kafka.PaymentEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pays for an order by debiting the customer's wallet balance.
 * This is an instant payment — no external gateway or webhook needed.
 * On success, publishes an OrderPaidEvent to Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayWalletUseCase {

    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;
    private final DebitWalletUseCase debitWalletUseCase;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public PaymentTransaction execute(PaymentTransaction inputTransaction) {
        // Fetch the latest state of the transaction with pessimistic lock to prevent double-debiting
        PaymentTransaction transaction = paymentTransactionRepository.findByIdForUpdate(inputTransaction.getId())
                .orElseThrow(() -> new mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentTransactionNotFoundException(inputTransaction.getId()));
                
        PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.from(transaction);

        // Idempotent — already paid
        if (aggregate.isPaid()) {
            log.info("Wallet payment {} already completed, skipping", transaction.getId());
            return transaction;
        }

        // Find and lock the customer's wallet
        Wallet wallet = getWalletByOwnerUseCase.execute(
                transaction.getCustomerId(), OwnerType.CUSTOMER);

        // Debit the wallet (will throw InsufficientBalanceException if not enough funds)
        debitWalletUseCase.execute(
                wallet.getId(),
                transaction.getMoney().getAmount(),
                transaction.getId(),
                "Payment for order " + transaction.getOrderId()
        );

        // Mark transaction as PAID
        aggregate.confirmWalletPayment();
        PaymentTransaction saved = paymentTransactionRepository.save(aggregate.getTransaction());

        publishPaymentProcessedEvent(saved);

        log.info("Wallet payment completed for order {} — debited {} from wallet {}",
                saved.getOrderId(), saved.getMoney().getAmount(), wallet.getId());
        return saved;
    }

    private void publishPaymentProcessedEvent(PaymentTransaction transaction) {
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
