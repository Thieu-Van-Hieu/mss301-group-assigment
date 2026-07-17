package mss301.se1911.group.assignment.paymentservices.application.usecase.payment;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PaymentTransactionNotFoundException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPaymentByIdUseCase {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Transactional(readOnly = true)
    public PaymentTransaction execute(UUID paymentTxId) {
        return paymentTransactionRepository.findById(paymentTxId)
                .orElseThrow(() -> new PaymentTransactionNotFoundException(paymentTxId));
    }
}
