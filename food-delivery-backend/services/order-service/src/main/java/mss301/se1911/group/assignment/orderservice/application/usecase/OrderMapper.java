package mss301.se1911.group.assignment.orderservice.application.usecase;

import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.orderservice.application.command.DeliveryInfoDto;
import mss301.se1911.group.assignment.orderservice.application.command.OrderItemDto;
import mss301.se1911.group.assignment.orderservice.application.command.PaymentInfoDto;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;

import java.util.List;

@Component
public class OrderMapper {
    
    public OrderResponse mapToResponse(OrderAggregate order) {
        if (order == null) return null;
        
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> OrderItemDto.builder()
                        .productId(item.getProductId())
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice().getAmount())
                        .currency(item.getPrice().getCurrency())
                        .build())
                .toList();
                
        DeliveryInfoDto deliveryInfoDto = null;
        if (order.getDeliveryInfo() != null) {
            deliveryInfoDto = DeliveryInfoDto.builder()
                    .address(order.getDeliveryInfo().getAddress())
                    .latitude(order.getDeliveryInfo().getLatitude())
                    .longitude(order.getDeliveryInfo().getLongitude())
                    .phone(order.getDeliveryInfo().getPhone())
                    .build();
        }
        
        PaymentInfoDto paymentInfoDto = null;
        if (order.getPaymentInfo() != null) {
            paymentInfoDto = PaymentInfoDto.builder()
                    .method(order.getPaymentInfo().getMethod())
                    .status(order.getPaymentInfo().getStatus())
                    .transactionId(order.getPaymentInfo().getTransactionId())
                    .build();
        }
        
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .items(itemDtos)
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount().getAmount())
                .currency(order.getTotalAmount().getCurrency())
                .deliveryInfo(deliveryInfoDto)
                .paymentInfo(paymentInfoDto)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
