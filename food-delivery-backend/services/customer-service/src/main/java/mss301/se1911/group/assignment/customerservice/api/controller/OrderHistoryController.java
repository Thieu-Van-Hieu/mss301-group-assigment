package mss301.se1911.group.assignment.customerservice.api.controller;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.application.dto.OrderHistoryResponse;
import mss301.se1911.group.assignment.customerservice.application.usecase.OrderHistoryQueryUseCases;
import mss301.se1911.group.assignment.customerservice.domain.repository.common.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers/{customerId}/orders")
@RequiredArgsConstructor
public class OrderHistoryController {

    private final OrderHistoryQueryUseCases orderHistoryQueryUseCases;

    @GetMapping
    public ResponseEntity<PageResult<OrderHistoryResponse>> listOrders(
            @PathVariable UUID customerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderHistoryQueryUseCases.listByCustomer(customerId, status, page, size));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderHistoryResponse> getOrderDetail(
            @PathVariable UUID customerId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderHistoryQueryUseCases.getDetail(customerId, orderId));
    }
}
