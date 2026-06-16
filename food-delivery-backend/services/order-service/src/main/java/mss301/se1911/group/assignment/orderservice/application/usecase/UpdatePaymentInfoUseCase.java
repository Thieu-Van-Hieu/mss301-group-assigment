package mss301.se1911.group.assignment.orderservice.application.usecase;

import mss301.se1911.group.assignment.orderservice.application.command.UpdatePaymentInfoCommand;

public interface UpdatePaymentInfoUseCase {
    OrderResponse execute(UpdatePaymentInfoCommand command);
}
