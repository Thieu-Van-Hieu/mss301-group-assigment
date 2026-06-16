package mss301.se1911.group.assignment.orderservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import mss301.se1911.group.assignment.orderservice.domain.aggregate.*;
import mss301.se1911.group.assignment.orderservice.domain.repository.OrderRepository;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentInfo;
import mss301.se1911.group.assignment.orderservice.domain.vo.PaymentStatus;
import mss301.se1911.group.assignment.orderservice.application.usecase.OrderEventPublisher;
import mss301.se1911.group.assignment.commonevents.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSagaEventListener {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @KafkaListener(topics = "payment-events-topic", groupId = "order-service-group")
    public void handlePaymentEvents(Object message) {
        log.info("Received message from payment-events-topic: {}", message);
        try {
            if (message instanceof PaymentProcessedKafkaEvent event) {
                log.info("Processing PaymentProcessedKafkaEvent for Order: {}", event.orderId());
                OrderAggregate order = orderRepository.findById(event.orderId()).orElse(null);
                if (order != null) {
                    order.updatePaymentInfo(new PaymentInfo(order.getPaymentInfo().getMethod(), PaymentStatus.PAID, event.transactionId()));
                    order.pay();
                    orderRepository.save(order);
                    eventPublisher.publish(new OrderPaidEvent(order));
                    log.info("Order {} transitioned to PAID.", event.orderId());
                } else {
                    log.error("Order not found: {}", event.orderId());
                }
            } else if (message instanceof PaymentFailedKafkaEvent event) {
                log.warn("Processing PaymentFailedKafkaEvent for Order: {}, Reason: {}", event.orderId(), event.reason());
                OrderAggregate order = orderRepository.findById(event.orderId()).orElse(null);
                if (order != null) {
                    order.cancel(event.reason());
                    orderRepository.save(order);
                    eventPublisher.publish(new OrderCancelledEvent(order, event.reason()));
                    log.info("Order {} cancelled due to payment failure.", event.orderId());
                } else {
                    log.error("Order not found: {}", event.orderId());
                }
            }
        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "delivery-events-topic", groupId = "order-service-group")
    public void handleDeliveryEvents(Object message) {
        log.info("Received message from delivery-events-topic: {}", message);
        try {
            if (message instanceof DeliveryCreatedKafkaEvent event) {
                log.info("Processing DeliveryCreatedKafkaEvent for Order: {}", event.orderId());
                OrderAggregate order = orderRepository.findById(event.orderId()).orElse(null);
                if (order != null) {
                    order.confirm();
                    order.startPreparing();
                    orderRepository.save(order);
                    eventPublisher.publish(new OrderConfirmedEvent(order));
                    log.info("Order {} confirmed and started preparing (delivery assigned to: {}).", event.orderId(), event.driverName());
                } else {
                    log.error("Order not found: {}", event.orderId());
                }
            } else if (message instanceof DeliveryFailedKafkaEvent event) {
                log.warn("Processing DeliveryFailedKafkaEvent for Order: {}, Reason: {}", event.orderId(), event.reason());
                OrderAggregate order = orderRepository.findById(event.orderId()).orElse(null);
                if (order != null) {
                    order.cancel(event.reason());
                    orderRepository.save(order);
                    eventPublisher.publish(new OrderCancelledEvent(order, event.reason()));
                    log.info("Order {} cancelled due to delivery failure.", event.orderId());
                } else {
                    log.error("Order not found: {}", event.orderId());
                }
            }
        } catch (Exception e) {
            log.error("Error processing delivery event: {}", e.getMessage(), e);
        }
    }
}
