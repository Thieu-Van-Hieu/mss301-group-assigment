package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.CancelOrderCommand;

public interface CancelOrderUseCase {
    OrderResponse execute(CancelOrderCommand command);
}
