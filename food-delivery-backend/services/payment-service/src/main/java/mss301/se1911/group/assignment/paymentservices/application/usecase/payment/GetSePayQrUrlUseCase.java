package mss301.se1911.group.assignment.paymentservices.application.usecase.payment;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.aggregate.PaymentTransactionAggregate;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PaymentTransactionRepository;
import mss301.se1911.group.assignment.paymentservices.infrastructure.adapter.sepay.SePayAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSePayQrUrlUseCase {

    private final GetPaymentByIdUseCase getPaymentByIdUseCase;
    private final SePayAdapter sePayAdapter;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Transactional
    public String execute(UUID paymentTxId) {
        PaymentTransaction transaction = getPaymentByIdUseCase.execute(paymentTxId);
        PaymentTransactionAggregate aggregate = PaymentTransactionAggregate.from(transaction);

        aggregate.validateSePayMethod();
        aggregate.markProcessing();

        String url = sePayAdapter.generateQrUrl(aggregate.getTransaction());
        paymentTransactionRepository.save(aggregate.getTransaction());

        return url;
    }
}
