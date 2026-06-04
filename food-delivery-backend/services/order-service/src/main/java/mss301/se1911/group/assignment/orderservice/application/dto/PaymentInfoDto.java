package mss301.se1911.group.assignment.orderservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mss301.se1911.group.assignment.orderservice.domain.model.PaymentMethod;
import mss301.se1911.group.assignment.orderservice.domain.model.PaymentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDto {
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
}
