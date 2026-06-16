package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.PayOrderCommand;

public interface PayOrderUseCase {
    OrderResponse execute(PayOrderCommand command);
}
