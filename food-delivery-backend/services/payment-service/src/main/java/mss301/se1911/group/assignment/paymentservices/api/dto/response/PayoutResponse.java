package mss301.se1911.group.assignment.paymentservices.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PayoutResponse {
    private UUID id;
    private UUID orderId;
    private UUID restaurantId;
    private UUID driverId;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal platformFee;
    private BigDecimal restaurantPayout;
    private BigDecimal driverPayout;
    private String status;
    private OffsetDateTime processedAt;
}
