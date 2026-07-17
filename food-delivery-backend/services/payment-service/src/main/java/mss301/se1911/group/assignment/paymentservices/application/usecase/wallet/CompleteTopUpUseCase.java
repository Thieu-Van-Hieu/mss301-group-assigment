package mss301.se1911.group.assignment.paymentservices.application.usecase.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Completes a top-up after the payment gateway confirms payment.
 * Marks the PaymentTransaction as PAID and credits the wallet.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompleteTopUpUseCase {

    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;
    private final CreditWalletUseCase creditWalletUseCase;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Transactional
    public void execute(PaymentTransaction transaction) {
        PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.from(transaction);

        if (!aggregate.isTopUp()) {
            throw new PaymentProcessingException(
                    "Transaction " + transaction.getId() + " is not a top-up transaction");
        }

        // Find the customer's wallet
        Wallet wallet = getWalletByOwnerUseCase.execute(
                transaction.getCustomerId(), OwnerType.CUSTOMER);

        // Credit the wallet with the top-up amount
        creditWalletUseCase.execute(
                wallet.getId(),
                transaction.getMoney().getAmount(),
                transaction.getId(),
                "Wallet top-up via " + transaction.getPaymentMethod()
        );

        log.info("Completed top-up {} — credited {} to wallet {} for customer {}",
                transaction.getId(), transaction.getMoney().getAmount(),
                wallet.getId(), transaction.getCustomerId());
    }
}
