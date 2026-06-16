package mss301.se1911.group.assignment.orderservice.application.usecase;

import java.util.List;

public interface GetAllOrdersUseCase {
    List<OrderResponse> execute();
}
