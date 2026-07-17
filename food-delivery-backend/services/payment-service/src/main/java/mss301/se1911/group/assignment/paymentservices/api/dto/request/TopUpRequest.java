package mss301.se1911.group.assignment.paymentservices.api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PaymentTransaction.PaymentGateway;

import java.math.BigDecimal;

@Data
public class TopUpRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10000", message = "Minimum top-up amount is 10,000 VND")
    @DecimalMax(value = "50000000", message = "Maximum top-up amount is 50,000,000 VND")
    private BigDecimal amount;

    @NotNull(message = "Gateway method is required")
    private PaymentGateway gatewayMethod;
}
