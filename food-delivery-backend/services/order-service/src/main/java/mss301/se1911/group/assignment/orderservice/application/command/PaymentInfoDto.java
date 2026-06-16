package mss301.se1911.group.assignment.orderservice.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentMethod;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDto {
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
}
