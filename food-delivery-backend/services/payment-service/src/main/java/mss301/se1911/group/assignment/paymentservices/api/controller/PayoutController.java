package mss301.se1911.group.assignment.paymentservices.api.controller;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.ApiResponse;
import mss301.se1911.group.assignment.paymentservices.api.dto.response.PayoutResponse;
import mss301.se1911.group.assignment.paymentservices.application.usecase.payout.GetPayoutByOrderIdUseCase;
import mss301.se1911.group.assignment.paymentservices.domain.entity.PayoutRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final GetPayoutByOrderIdUseCase getPayoutByOrderIdUseCase;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PayoutResponse>> getPayoutByOrder(@PathVariable("orderId") UUID orderId) {
        PayoutRecord record = getPayoutByOrderIdUseCase.execute(orderId);
        
        PayoutResponse response = PayoutResponse.builder()
                .id(record.getId())
                .orderId(record.getOrderId())
                .restaurantId(record.getRestaurantId())
                .driverId(record.getDriverId())
                .totalAmount(record.getBreakdown().getTotalAmount())
                .deliveryFee(record.getBreakdown().getDeliveryFee())
                .platformFee(record.getBreakdown().getPlatformFee())
                .restaurantPayout(record.getBreakdown().getRestaurantPayout())
                .driverPayout(record.getBreakdown().getDriverPayout())
                .status(record.getStatus().name())
                .processedAt(record.getProcessedAt())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
