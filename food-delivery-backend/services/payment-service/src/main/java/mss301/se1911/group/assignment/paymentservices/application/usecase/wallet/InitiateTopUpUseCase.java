package mss301.se1911.group.assignment.paymentservices.application.usecase.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.application.command.InitiateTopUpCommand;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentGateway;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.exception.InvalidTopUpMethodException;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentProcessingException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Creates a top-up PaymentTransaction that the customer pays via a gateway (SEPAY/PAYOS/MOMO).
 * Upon successful gateway webhook, CompleteTopUpUseCase credits the wallet.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InitiateTopUpUseCase {

    private static final Set<PaymentGateway> ALLOWED_GATEWAY_METHODS =
            Set.of(PaymentGateway.SEPAY);

    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Transactional
    public PaymentTransaction execute(InitiateTopUpCommand command) {
        // Validate gateway method — only external gateways are allowed for top-up
        if (!ALLOWED_GATEWAY_METHODS.contains(command.gatewayMethod())) {
            throw new InvalidTopUpMethodException(command.gatewayMethod());
        }

        // Verify customer has an active wallet
        Wallet wallet = getWalletByOwnerUseCase.execute(command.customerId(), OwnerType.CUSTOMER);
        if (wallet.getStatus() != Wallet.WalletStatus.ACTIVE) {
            throw new PaymentProcessingException(
                    "Cannot top up wallet in status: " + wallet.getStatus());
        }

        // Create top-up transaction
        PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.initiateTopUp(
                command.customerId(), command.amount(), command.gatewayMethod());

        PaymentTransaction transaction = paymentTransactionRepository.save(aggregate.getTransaction());
        log.info("Initiated top-up transaction {} for customer {} via {} — amount: {}",
                transaction.getId(), command.customerId(), command.gatewayMethod(), command.amount());
        return transaction;
    }
}
