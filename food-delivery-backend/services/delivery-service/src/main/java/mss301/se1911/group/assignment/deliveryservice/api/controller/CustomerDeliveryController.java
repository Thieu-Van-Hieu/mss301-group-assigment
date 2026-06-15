package mss301.se1911.group.assignment.deliveryservice.api.controller;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.api.dto.request.CancelDeliveryRequest;
import mss301.se1911.group.assignment.deliveryservice.api.dto.response.DeliveryWebResponse;
import mss301.se1911.group.assignment.deliveryservice.application.dto.DeliveryResponse;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.CancelDeliveryUseCases;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.DeliveryQueryUseCases;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/deliveries")
@RequiredArgsConstructor
public class CustomerDeliveryController {

    private final DeliveryQueryUseCases deliveryQueryUseCases;
    private final CancelDeliveryUseCases cancelDeliveryUseCases;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryWebResponse> getTrackingInfo(@PathVariable UUID orderId) {
        DeliveryResponse appDto = deliveryQueryUseCases.getCustomerTrackingInfo(orderId);

        if (appDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(DeliveryWebResponse.fromAppDto(appDto));
    }

    @PostMapping("/{deliveryId}/cancel")
    public ResponseEntity<String> cancelByCustomer(
            @PathVariable UUID deliveryId,
            @RequestBody CancelDeliveryRequest request) { // Dùng Web Request

        cancelDeliveryUseCases.cancelByCustomer(deliveryId, request.reason());
        return ResponseEntity.ok("Đã hủy chuyến giao hàng thành công.");
    }
}
