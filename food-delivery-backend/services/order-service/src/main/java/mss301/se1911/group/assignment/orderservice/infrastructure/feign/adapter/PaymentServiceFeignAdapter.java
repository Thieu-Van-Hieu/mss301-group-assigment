package mss301.se1911.group.assignment.orderservice.infrastructure.feign.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.orderservice.application.command.PaymentInfoDto;
import mss301.se1911.group.assignment.orderservice.application.usecase.PaymentServicePort;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentStatus;
import mss301.se1911.group.assignment.orderservice.infrastructure.feign.client.PaymentFeignClient;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceFeignAdapter implements PaymentServicePort {

    private final PaymentFeignClient paymentFeignClient;

    @Override
    public PaymentInfoDto processPayment(UUID orderId, BigDecimal amount, String currency, PaymentInfoDto paymentInfo) {
        log.info("Processing payment for Order ID: {}, Amount: {} {}", orderId, amount, currency);
        
        // TODO: Gọi sang payment service qua Feign Client
        // PaymentFeignClient.PaymentRequest request = new PaymentFeignClient.PaymentRequest(orderId, amount, currency, paymentInfo);
        // return paymentFeignClient.processPayment(request);

        return PaymentInfoDto.builder()
                .method(paymentInfo.getMethod())
                .status(PaymentStatus.PAID)
                .transactionId("MOCK-TXN-" + UUID.randomUUID().toString().substring(0, 8))
                .build();
    }
}
