package mss301.se1911.group.assignment.orderservice.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateItemCommand {
    private UUID orderId;
    private OrderItemDto itemDto;
}
