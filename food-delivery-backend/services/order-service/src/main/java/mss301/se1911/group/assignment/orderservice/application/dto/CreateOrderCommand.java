package mss301.se1911.group.assignment.orderservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderCommand {
    private UUID customerId;
    private UUID restaurantId;
    private List<OrderItemDto> items;
    private DeliveryInfoDto deliveryInfo;
    private PaymentInfoDto paymentInfo;
}
