package mss301.se1911.group.assignment.orderservice.api.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mss301.se1911.group.assignment.orderservice.application.dto.*;
import mss301.se1911.group.assignment.orderservice.application.ports.in.OrderUseCase;
import mss301.se1911.group.assignment.orderservice.domain.model.OrderStatus;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderCommand command) {
        log.info("REST request to create order: {}", command);
        OrderResponse response = orderUseCase.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") UUID id) {
        log.info("REST request to get order: {}", id);
        return ResponseEntity.ok(orderUseCase.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("REST request to get all orders");
        return ResponseEntity.ok(orderUseCase.getAllOrders());
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addOrUpdateItem(@PathVariable("id") UUID id, @RequestBody OrderItemDto itemDto) {
        log.info("REST request to add/update item in order {}: {}", id, itemDto);
        return ResponseEntity.ok(orderUseCase.addOrUpdateItem(id, itemDto));
    }

    @DeleteMapping("/{id}/items/{productId}")
    public ResponseEntity<OrderResponse> removeItem(@PathVariable("id") UUID id, @PathVariable("productId") UUID productId) {
        log.info("REST request to remove item {} from order {}", productId, id);
        return ResponseEntity.ok(orderUseCase.removeItem(id, productId));
    }

    @PutMapping("/{id}/delivery")
    public ResponseEntity<OrderResponse> updateDelivery(@PathVariable("id") UUID id, @RequestBody DeliveryInfoDto deliveryInfoDto) {
        log.info("REST request to update delivery in order {}: {}", id, deliveryInfoDto);
        return ResponseEntity.ok(orderUseCase.updateDeliveryInfo(id, deliveryInfoDto));
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<OrderResponse> updatePayment(@PathVariable("id") UUID id, @RequestBody PaymentInfoDto paymentInfoDto) {
        log.info("REST request to update payment in order {}: {}", id, paymentInfoDto);
        return ResponseEntity.ok(orderUseCase.updatePaymentInfo(id, paymentInfoDto));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable("id") UUID id) {
        log.info("REST request to pay order: {}", id);
        return ResponseEntity.ok(orderUseCase.payOrder(id));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable("id") UUID id) {
        log.info("REST request to confirm order: {}", id);
        return ResponseEntity.ok(orderUseCase.confirmOrder(id));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable("id") UUID id, @RequestParam("status") OrderStatus status) {
        log.info("REST request to update status of order {} to {}", id, status);
        return ResponseEntity.ok(orderUseCase.updateOrderStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable("id") UUID id, @RequestParam("reason") String reason) {
        log.info("REST request to cancel order {} with reason: {}", id, reason);
        return ResponseEntity.ok(orderUseCase.cancelOrder(id, reason));
    }
}
