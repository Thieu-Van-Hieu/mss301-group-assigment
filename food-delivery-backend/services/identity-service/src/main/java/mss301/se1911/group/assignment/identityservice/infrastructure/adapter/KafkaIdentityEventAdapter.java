package mss301.se1911.group.assignment.identityservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.identityservice.domain.event.UserCreatedEvent;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaIdentityEventAdapter implements IdentityEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaIdentityEventAdapter.class);

    // Tên Topic tập trung quản lý việc tạo tài khoản, các service khác sẽ subscribe topic này
    private static final String TOPIC = "user-onboarding-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        log.info("Preparing to send UserCreatedEvent to Kafka for username: {}", event.username());

        try {
            // Bắn Object sự kiện dạng JSON sang Kafka, dùng userId làm Message Key để đảm bảo tính tuần tự
            kafkaTemplate.send(TOPIC, event.userId(), event);
            log.info("Successfully published UserCreatedEvent to topic [{}] for partition key: {}", TOPIC, event.userId());
        } catch (Exception e) {
            // Trong thực tế, nếu bắn Kafka lỗi thì ta nên log lại hoặc ném vào hàng đợi bù (Retry DLT)
            log.error("Failed to send event to Kafka topic [{}]: {}", TOPIC, e.getMessage(), e);
        }
    }
}