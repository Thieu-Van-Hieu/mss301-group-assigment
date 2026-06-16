package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.UpdateOrderStatusCommand;

public interface UpdateOrderStatusUseCase {
    OrderResponse execute(UpdateOrderStatusCommand command);
}
