package mss301.se1911.group.assignment.paymentservices.application.usecase.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.CreditWalletUseCase;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.GetWalletByOwnerUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentStatus;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessRefundUseCase {

    private final GetPaymentByOrderIdUseCase getPaymentByOrderIdUseCase;
    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;
    private final CreditWalletUseCase creditWalletUseCase;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Transactional
    public PaymentTransaction execute(UUID orderId) {
        PaymentTransaction transaction = getPaymentByOrderIdUseCase.execute(orderId);
        PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.from(transaction);

        if (aggregate.getTransaction().getStatus() == PaymentStatus.REFUNDED) {
            return aggregate.getTransaction();
        }

        // For SEPAY, if paid, we refund to internal wallet
        if (aggregate.isSePay() && aggregate.isPaid()) {
            Wallet customerWallet = getWalletByOwnerUseCase.execute(transaction.getCustomerId(), OwnerType.CUSTOMER);
            creditWalletUseCase.execute(
                    customerWallet.getId(),
                    transaction.getMoney().getAmount(),
                    transaction.getId(),
                    "Refund for cancelled order " + orderId
            );
        }

        aggregate.markRefunded();
        PaymentTransaction saved = paymentTransactionRepository.save(aggregate.getTransaction());
        log.info("Processed refund for payment {} (Order: {})", saved.getId(), orderId);
        return saved;
    }
}
