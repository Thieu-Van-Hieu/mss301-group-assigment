package mss301.se1911.group.assignment.paymentservices.application.usecase.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonevents.PaymentProcessedKafkaEvent;
import mss301.se1911.group.assignment.paymentservices.application.command.ProcessSePayWebhookCommand;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.CompleteTopUpUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.SePayValidationException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.kafka.PaymentEventProducer;
import mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.sepay.SePayAdapter;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.CreditWalletUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.GetWalletByOwnerUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessSePayWebhookUseCase {

    private final SePayAdapter sePayAdapter;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final CompleteTopUpUseCase completeTopUpUseCase;
    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;
    private final CreditWalletUseCase creditWalletUseCase;

    @Transactional
    public PaymentTransaction execute(ProcessSePayWebhookCommand command) {
        // 1. Authenticate webhook using Apikey header
        sePayAdapter.validateWebhook(command.authHeader());

        // 2. Extract transaction ID from transfer content
        String content = command.content();
        String txnRefStr = sePayAdapter.extractTxnRef(content);

        if (txnRefStr == null) {
            log.warn("Could not extract payment ID from SePay webhook content: {}", content);
            throw new PaymentProcessingException("Missing or invalid payment ID in SePay content");
        }

        UUID txnRef = UUID.fromString(txnRefStr);
        PaymentTransaction transaction = paymentTransactionRepository.findByIdForUpdate(txnRef)
                .orElseThrow(() -> new mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentTransactionNotFoundException(txnRef));
        PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.from(transaction);

        // 3. Idempotency: already PAID
        if (aggregate.isPaid()) {
            log.info("Transaction {} already PAID, skipping SePay webhook processing", txnRef);
            return aggregate.getTransaction();
        }

        // 4. Validate amount — marks FAILED and throws on mismatch (e.g., underpayment)
        try {
            aggregate.validateSePayAmount(command.transferAmount());
        } catch (SePayValidationException e) {
            paymentTransactionRepository.save(aggregate.getTransaction());
            log.error("Payment {} failed via SePay: {}", transaction.getId(), e.getMessage());
            throw e;
        }

        // 5. Mark PAID via gateway
        String gatewayTransId = String.valueOf(command.id());
        aggregate.markPaidByGateway(gatewayTransId, command.toString());
        PaymentTransaction saved = paymentTransactionRepository.save(aggregate.getTransaction());

        // 6. Route based on transaction type
        if (aggregate.isTopUp()) {
            aggregate.updateAmount(command.transferAmount());
            saved = paymentTransactionRepository.save(aggregate.getTransaction());
            completeTopUpUseCase.execute(saved);
        } else {
            BigDecimal expectedAmount = aggregate.getTransaction().getMoney().getAmount();
            BigDecimal overpaidAmount = command.transferAmount().subtract(expectedAmount);
            if (overpaidAmount.compareTo(BigDecimal.ZERO) > 0) {
                log.info("Order payment {} overpaid by {}, refunding to wallet", saved.getId(), overpaidAmount);
                Wallet customerWallet = getWalletByOwnerUseCase.execute(saved.getCustomerId(), OwnerType.CUSTOMER);
                creditWalletUseCase.execute(
                        customerWallet.getId(),
                        overpaidAmount,
                        saved.getId(),
                        "Refund overpaid amount for order " + saved.getOrderId()
                );
            }
            publishOrderPaidEvent(saved);
        }
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

