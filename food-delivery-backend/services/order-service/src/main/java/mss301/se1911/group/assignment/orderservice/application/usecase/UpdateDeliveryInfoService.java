package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.UpdateDeliveryInfoCommand;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;
import mss301.se1911.group.assignment.orderservice.domain.vo.DeliveryInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateDeliveryInfoService implements UpdateDeliveryInfoUseCase {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(UpdateDeliveryInfoCommand command) {
        log.info("Updating delivery info for order: {}", command.getOrderId());
        OrderAggregate order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + command.getOrderId()));
        
        DeliveryInfo deliveryInfo = new DeliveryInfo(
                command.getDeliveryInfoDto().getAddress(),
                command.getDeliveryInfoDto().getLatitude(),
                command.getDeliveryInfoDto().getLongitude(),
                command.getDeliveryInfoDto().getPhone()
        );
        
        order.updateDeliveryInfo(deliveryInfo);
        OrderAggregate savedOrder = orderRepository.save(order);
        return orderMapper.mapToResponse(savedOrder);
    }
}
