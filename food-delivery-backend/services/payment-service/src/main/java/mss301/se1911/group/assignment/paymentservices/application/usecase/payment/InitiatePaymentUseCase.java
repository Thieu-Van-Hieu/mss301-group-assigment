package mss301.se1911.group.assignment.paymentservices.application.usecase.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.paymentservices.application.command.InitiatePaymentCommand;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.exception.DuplicatePaymentException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentStatus;
import mss301.se1911.group.assignment.paymentservices.application.usecase.wallet.GetWalletByOwnerUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentMethod;
import mss301.se1911.group.assignment.paymentservices.domain.entity.Wallet;
import mss301.se1911.group.assignment.paymentservices.domain.exception.InsufficientBalanceException;
import mss301.se1911.group.assignment.paymentservices.domain.vo.OwnerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitiatePaymentUseCase {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final GetWalletByOwnerUseCase getWalletByOwnerUseCase;

    @Transactional
    public PaymentTransaction execute(InitiatePaymentCommand command) {
        // Prevent duplicate payment initiation for the same order
        if (paymentTransactionRepository.existsByOrderIdAndStatusIn(
                command.orderId(),
                Arrays.asList(PaymentStatus.PENDING, PaymentStatus.PROCESSING, PaymentStatus.PAID))) {
            throw new DuplicatePaymentException(command.orderId());
        }

        if (command.paymentMethod() == PaymentMethod.WALLET) {
            Wallet wallet = getWalletByOwnerUseCase.execute(command.customerId(), OwnerType.CUSTOMER);
            if (wallet.getBalance().getAmount().compareTo(command.amount()) < 0) {
                throw new InsufficientBalanceException(wallet.getId(), command.amount(), wallet.getBalance().getAmount());
            }
        }

        PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.initiate(
                command.orderId(), command.customerId(),
                command.amount(), command.paymentMethod(), command.paymentGateway());

        PaymentTransaction transaction = paymentTransactionRepository.save(aggregate.getTransaction());
        log.info("Initiated payment {} for order {}", transaction.getId(), command.orderId());
        return transaction;
    }
}
