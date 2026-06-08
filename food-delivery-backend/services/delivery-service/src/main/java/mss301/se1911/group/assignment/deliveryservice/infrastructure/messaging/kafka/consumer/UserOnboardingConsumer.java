package mss301.se1911.group.assignment.deliveryservice.infrastructure.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.deliveryservice.application.command.CreateDriverDraftCommand;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.CreateDriverDraftProfileUseCase;
import mss301.se1911.group.assignment.deliveryservice.infrastructure.messaging.kafka.event.UserOnboardingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserOnboardingConsumer {
    private final CreateDriverDraftProfileUseCase createDriverDraftProfileUseCase;

    /**
     * Lắng nghe Topic khi có tài khoản mới đăng ký thành công trong hệ thống tổng
     */
    @KafkaListener(
            topics = "${app.kafka.topics.user-onboarding}",
            groupId = "delivery-service-onboarding-group",
            containerFactory = "kafkaListenerContainerFactory" // Đảm bảo cấu hình JsonDeserializer hợp lệ
    )
    public void consumeUserOnboarding(UserOnboardingEvent event) {
        log.info("Nhận được Event đăng ký tài khoản mới: ID={}, Role={}", event.id(), event.role());

        try {
            // LUẬT: Chỉ quan tâm nếu Role là DRIVER. Nếu là CUSTOMER hoặc MERCHANT thì bỏ qua (Ignore)
            if (event.role() == null || !event.role().equalsIgnoreCase("DRIVER")) {
                log.info("Tài khoản ID={} có role là {}, không phải DRIVER -> Bỏ qua không xử lý.", event.id(), event.role());
                return;
            }

            log.info("Phát hiện tài xế mới đăng ký! Tiến hành tạo bản ghi nháp cho Driver ID: {}", event.id());

            // Chuyển đổi dữ liệu từ Event hạ tầng sang Command nghiệp vụ của Application
            CreateDriverDraftCommand command = new CreateDriverDraftCommand(
                    event.id(),
                    event.fullName(),
                    event.phoneNumber(),
                    event.email()
            );

            // Thực thi UseCase tạo hồ sơ nháp chờ điền form xe cộ
            createDriverDraftProfileUseCase.execute(command);
            log.info("Tạo thành công hồ sơ nháp cho Driver ID: {}", event.id());

        } catch (Exception e) {
            // Vì chạy bất đồng bộ qua Kafka, nếu lỗi cần ghi log cảnh báo chi tiết để sửa lỗi (Dead Letter Queue hoặc kiểm tra dữ liệu)
            log.error("Lỗi nghiêm trọng xảy ra khi xử lý event onboarding cho User {}: {}", event.id(), e.getMessage(), e);
        }
    }
}
