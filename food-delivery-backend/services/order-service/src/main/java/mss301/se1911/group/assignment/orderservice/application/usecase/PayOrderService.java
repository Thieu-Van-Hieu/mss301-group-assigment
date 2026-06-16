package mss301.se1911.group.assignment.orderservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import mss301.se1911.group.assignment.orderservice.application.command.PayOrderCommand;
import mss301.se1911.group.assignment.orderservice.application.command.PaymentInfoDto;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderAggregate;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.OrderPaidEvent;
import mss301.se1911.group.assignment.orderservice.domain.exception.OrderDomainException;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentInfo;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentMethod;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOrderService implements PayOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final PaymentServicePort paymentServicePort;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse execute(PayOrderCommand command) {
        log.info("Paying order: {}", command.getOrderId());
        OrderAggregate order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderDomainException("Order not found with id: " + command.getOrderId()));
        
        if (order.getPaymentInfo() == null) {
            throw new OrderDomainException("Cannot pay without payment info");
        }
        
        if (order.getPaymentInfo().getMethod() == PaymentMethod.CASH) {
            throw new OrderDomainException("Cash on delivery orders do not require pre-payment");
        }
        
        PaymentInfoDto requestPaymentInfo = PaymentInfoDto.builder()
                .method(order.getPaymentInfo().getMethod())
                .status(order.getPaymentInfo().getStatus())
                .transactionId(order.getPaymentInfo().getTransactionId())
                .build();
                
        PaymentInfoDto paymentResult = paymentServicePort.processPayment(
                order.getId(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                requestPaymentInfo
        );
        
        PaymentInfo updatedPaymentInfo = new PaymentInfo(
                paymentResult.getMethod(),
                paymentResult.getStatus(),
                paymentResult.getTransactionId()
        );
        order.updatePaymentInfo(updatedPaymentInfo);
        
        if (paymentResult.getStatus() == PaymentStatus.PAID) {
            order.pay();
        } else {
            throw new OrderDomainException("Payment failed for order: " + command.getOrderId());
        }
        
        OrderAggregate savedOrder = orderRepository.save(order);
        eventPublisher.publish(new OrderPaidEvent(savedOrder));
        return orderMapper.mapToResponse(savedOrder);
    }
}
