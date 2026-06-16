package mss301.se1911.group.assignment.orderservice.infrastructure.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import mss301.se1911.group.assignment.orderservice.infrastructure.feign.config.FeignClientConfig;
import mss301.se1911.group.assignment.orderservice.application.command.PaymentInfoDto;

@FeignClient(name = "payment-service", configuration = FeignClientConfig.class)
public interface PaymentFeignClient {

    @PostMapping("/api/payments/process")
    PaymentInfoDto processPayment(@RequestBody PaymentRequest request);

    class PaymentRequest {
        private java.util.UUID orderId;
        private java.math.BigDecimal amount;
        private String currency;
        private PaymentInfoDto paymentInfo;

        public PaymentRequest() {}

        public PaymentRequest(java.util.UUID orderId, java.math.BigDecimal amount, String currency, PaymentInfoDto paymentInfo) {
            this.orderId = orderId;
            this.amount = amount;
            this.currency = currency;
            this.paymentInfo = paymentInfo;
        }

        public java.util.UUID getOrderId() { return orderId; }
        public void setOrderId(java.util.UUID orderId) { this.orderId = orderId; }
        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public PaymentInfoDto getPaymentInfo() { return paymentInfo; }
        public void setPaymentInfo(PaymentInfoDto paymentInfo) { this.paymentInfo = paymentInfo; }
    }
}
