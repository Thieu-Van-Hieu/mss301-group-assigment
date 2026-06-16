package mss301.se1911.group.assignment.orderservice.api.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mss301.se1911.group.assignment.orderservice.application.command.*;
import mss301.se1911.group.assignment.orderservice.application.usecase.*;
import mss301.se1911.group.assignment.orderservice.domain.vo.OrderStatus;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetAllOrdersUseCase getAllOrdersUseCase;
    private final AddOrUpdateItemUseCase addOrUpdateItemUseCase;
    private final RemoveItemUseCase removeItemUseCase;
    private final UpdateDeliveryInfoUseCase updateDeliveryInfoUseCase;
    private final UpdatePaymentInfoUseCase updatePaymentInfoUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderCommand command) {
        log.info("REST request to create order: {}", command);
        OrderResponse response = createOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") UUID id) {
        log.info("REST request to get order: {}", id);
        return ResponseEntity.ok(getOrderByIdUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("REST request to get all orders");
        return ResponseEntity.ok(getAllOrdersUseCase.execute());
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addOrUpdateItem(@PathVariable("id") UUID id, @RequestBody OrderItemDto itemDto) {
        log.info("REST request to add/update item in order {}: {}", id, itemDto);
        AddOrUpdateItemCommand command = new AddOrUpdateItemCommand(id, itemDto);
        return ResponseEntity.ok(addOrUpdateItemUseCase.execute(command));
    }

    @DeleteMapping("/{id}/items/{productId}")
    public ResponseEntity<OrderResponse> removeItem(@PathVariable("id") UUID id, @PathVariable("productId") UUID productId) {
        log.info("REST request to remove item {} from order {}", productId, id);
        RemoveItemCommand command = new RemoveItemCommand(id, productId);
        return ResponseEntity.ok(removeItemUseCase.execute(command));
    }

    @PutMapping("/{id}/delivery")
    public ResponseEntity<OrderResponse> updateDelivery(@PathVariable("id") UUID id, @RequestBody DeliveryInfoDto deliveryInfoDto) {
        log.info("REST request to update delivery in order {}: {}", id, deliveryInfoDto);
        UpdateDeliveryInfoCommand command = new UpdateDeliveryInfoCommand(id, deliveryInfoDto);
        return ResponseEntity.ok(updateDeliveryInfoUseCase.execute(command));
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<OrderResponse> updatePayment(@PathVariable("id") UUID id, @RequestBody PaymentInfoDto paymentInfoDto) {
        log.info("REST request to update payment in order {}: {}", id, paymentInfoDto);
        UpdatePaymentInfoCommand command = new UpdatePaymentInfoCommand(id, paymentInfoDto);
        return ResponseEntity.ok(updatePaymentInfoUseCase.execute(command));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable("id") UUID id) {
        log.info("REST request to pay order: {}", id);
        PayOrderCommand command = new PayOrderCommand(id);
        return ResponseEntity.ok(payOrderUseCase.execute(command));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable("id") UUID id) {
        log.info("REST request to confirm order: {}", id);
        ConfirmOrderCommand command = new ConfirmOrderCommand(id);
        return ResponseEntity.ok(confirmOrderUseCase.execute(command));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable("id") UUID id, @RequestParam("status") OrderStatus status) {
        log.info("REST request to update status of order {} to {}", id, status);
        UpdateOrderStatusCommand command = new UpdateOrderStatusCommand(id, status);
        return ResponseEntity.ok(updateOrderStatusUseCase.execute(command));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable("id") UUID id, @RequestParam("reason") String reason) {
        log.info("REST request to cancel order {} with reason: {}", id, reason);
        CancelOrderCommand command = new CancelOrderCommand(id, reason);
        return ResponseEntity.ok(cancelOrderUseCase.execute(command));
    }
}
