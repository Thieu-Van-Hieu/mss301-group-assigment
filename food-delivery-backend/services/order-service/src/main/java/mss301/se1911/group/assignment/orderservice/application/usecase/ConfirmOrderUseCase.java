package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.ConfirmOrderCommand;

public interface ConfirmOrderUseCase {
    OrderResponse execute(ConfirmOrderCommand command);
}
