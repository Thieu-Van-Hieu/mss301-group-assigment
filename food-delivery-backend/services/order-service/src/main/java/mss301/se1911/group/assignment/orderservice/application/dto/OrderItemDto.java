package mss301.se1911.group.assignment.orderservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID productId;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String currency;
}
