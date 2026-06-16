package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.RemoveItemCommand;

public interface RemoveItemUseCase {
    OrderResponse execute(RemoveItemCommand command);
}
