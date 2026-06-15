package mss301.se1911.group.assignment.deliveryservice.api.controller;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.api.dto.request.CreateDeliveryRequest;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.CreateDeliveryUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/deliveries")
@RequiredArgsConstructor
public class DeliveryInternalController {

    private final CreateDeliveryUseCase createDeliveryUseCase;

    @PostMapping
    public ResponseEntity<String> createDelivery(@RequestBody CreateDeliveryRequest request) {
        // Nhận Request của Presentation -> Map sang Command của Application -> Gọi UseCase
        createDeliveryUseCase.execute(request.toCommand());

        return ResponseEntity.ok("Tạo chuyến giao hàng thành công, đang chờ điều phối!");
    }
}