package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.UpdateDeliveryInfoCommand;

public interface UpdateDeliveryInfoUseCase {
    OrderResponse execute(UpdateDeliveryInfoCommand command);
}
