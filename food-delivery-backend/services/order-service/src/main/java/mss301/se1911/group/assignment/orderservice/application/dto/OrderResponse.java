package mss301.se1911.group.assignment.orderservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mss301.se1911.group.assignment.orderservice.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private List<OrderItemDto> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String currency;
    private DeliveryInfoDto deliveryInfo;
    private PaymentInfoDto paymentInfo;
    private LocalDateTime createdAt;
}
