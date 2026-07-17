package mss301.se1911.group.assignment.customerservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.customerservice.application.command.RecordOrderCommand;
import mss301.se1911.group.assignment.customerservice.domain.entity.OrderHistoryEntity;
import mss301.se1911.group.assignment.customerservice.domain.repository.OrderHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Dựng và cập nhật read model lịch sử đơn hàng dựa trên event nhận từ Order Service.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderHistoryProjectionUseCases {

    private final OrderHistoryRepository orderHistoryRepository;

    @Transactional
    public void recordCreated(RecordOrderCommand command) {
        // Idempotent: nếu đã ghi nhận đơn này rồi thì bỏ qua (tránh xử lý trùng event)
        if (orderHistoryRepository.findById(command.orderId()).isPresent()) {
            log.info("Đơn {} đã tồn tại trong lịch sử, bỏ qua.", command.orderId());
            return;
        }

        OrderHistoryEntity entity = OrderHistoryEntity.builder()
                .orderId(command.orderId())
                .customerId(command.customerId())
                .restaurantId(command.restaurantId())
                .status(command.status() != null ? command.status() : "CREATED")
                .totalAmount(command.totalAmount())
                .currency(command.currency())
                .itemsSummary(command.itemsSummary())
                .createdAt(command.createdAt() != null ? command.createdAt() : ZonedDateTime.now())
                .build();

        orderHistoryRepository.save(entity);
        log.info("Đã ghi nhận đơn mới {} vào lịch sử của khách {}", command.orderId(), command.customerId());
    }

    @Transactional
    public void markCompleted(UUID orderId, ZonedDateTime completedAt) {
        orderHistoryRepository.findById(orderId).ifPresentOrElse(entity -> {
            entity.setStatus("COMPLETED");
            entity.setCompletedAt(completedAt != null ? completedAt : ZonedDateTime.now());
            orderHistoryRepository.save(entity);
            log.info("Đã cập nhật đơn {} sang trạng thái COMPLETED.", orderId);
        }, () -> log.warn("Nhận event hoàn tất cho đơn {} nhưng chưa có trong lịch sử.", orderId));
    }
}
