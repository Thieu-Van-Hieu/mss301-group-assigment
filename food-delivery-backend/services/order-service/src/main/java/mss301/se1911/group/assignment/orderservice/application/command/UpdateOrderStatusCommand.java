package mss301.se1911.group.assignment.orderservice.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mss301.se1911.group.assignment.orderservice.domain.vo.OrderStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusCommand {
    private UUID orderId;
    private OrderStatus status;
}
