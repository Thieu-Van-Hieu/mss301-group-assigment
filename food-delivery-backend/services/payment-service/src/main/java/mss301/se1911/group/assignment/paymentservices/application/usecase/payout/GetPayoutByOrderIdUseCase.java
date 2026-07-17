package mss301.se1911.group.assignment.paymentservices.application.usecase.payout;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import mss301.se1911.group.assignment.paymentservices.domain.exception.PayoutRecordNotFoundException;
import mss301.se1911.group.assignment.paymentservices.domain.repository.PayoutRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPayoutByOrderIdUseCase {

    private final PayoutRecordRepository payoutRecordRepository;

    @Transactional(readOnly = true)
    public PayoutRecord execute(UUID orderId) {
        return payoutRecordRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PayoutRecordNotFoundException("orderId", orderId));
    }
}
