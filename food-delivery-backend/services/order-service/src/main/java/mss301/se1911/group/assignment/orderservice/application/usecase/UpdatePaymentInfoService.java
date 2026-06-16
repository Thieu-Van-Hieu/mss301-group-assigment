package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.UpdatePaymentInfoCommand;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePaymentInfoService implements UpdatePaymentInfoUseCase {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(UpdatePaymentInfoCommand command) {
        log.info("Updating payment info for order: {}", command.getOrderId());
        OrderAggregate order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + command.getOrderId()));
        
        PaymentInfo paymentInfo = new PaymentInfo(
                command.getPaymentInfoDto().getMethod(),
                command.getPaymentInfoDto().getStatus(),
                command.getPaymentInfoDto().getTransactionId()
        );
        
        order.updatePaymentInfo(paymentInfo);
        OrderAggregate savedOrder = orderRepository.save(order);
        return orderMapper.mapToResponse(savedOrder);
    }
}
