package mss301.se1911.group.assignment.orderservice.application.ports.in;

import mss301.se1911.group.assignment.orderservice.application.dto.*;
import mss301.se1911.group.assignment.orderservice.domain.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderUseCase {
    OrderResponse createOrder(CreateOrderCommand command);
    OrderResponse getOrderById(UUID id);
    List<OrderResponse> getAllOrders();
    OrderResponse addOrUpdateItem(UUID id, OrderItemDto itemDto);
    OrderResponse removeItem(UUID id, UUID productId);
    OrderResponse updateDeliveryInfo(UUID id, DeliveryInfoDto deliveryInfoDto);
    OrderResponse updatePaymentInfo(UUID id, PaymentInfoDto paymentInfoDto);
    OrderResponse payOrder(UUID id);
    OrderResponse confirmOrder(UUID id);
    OrderResponse updateOrderStatus(UUID id, OrderStatus status);
    OrderResponse cancelOrder(UUID id, String reason);
}
