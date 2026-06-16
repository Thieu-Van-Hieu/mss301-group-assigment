package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.CreateOrderCommand;

public interface CreateOrderUseCase {
    OrderResponse execute(CreateOrderCommand command);
}
