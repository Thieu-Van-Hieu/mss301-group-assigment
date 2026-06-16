package mss301.se1911.group.assignment.orderservice.application.usecase;

import java.util.UUID;

public interface GetOrderByIdUseCase {
    OrderResponse execute(UUID id);
}
