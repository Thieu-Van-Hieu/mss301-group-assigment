package mss301.se1911.group.assignment.customerservice.infrastructure.messaging.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.customerservice.application.command.CreateCustomerDraftCommand;
import mss301.se1911.group.assignment.customerservice.application.usecase.CreateCustomerDraftUseCase;
import mss301.se1911.group.assignment.customerservice.infrastructure.messaging.kafka.event.UserOnboardingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserOnboardingConsumer {

    private final CreateCustomerDraftUseCase createCustomerDraftUseCase;

    /**
     * Lắng nghe khi có tài khoản mới đăng ký. Chỉ xử lý nếu role là CUSTOMER.
     */
    @KafkaListener(topics = "${app.kafka.topics.user-onboarding}", groupId = "customer-service-onboarding-group")
    public void consume(UserOnboardingEvent event) {
        log.info("Nhận event onboarding: userId={}, role={}", event.userId(), event.role());

        try {
            if (event.role() == null || !event.role().equalsIgnoreCase("CUSTOMER")) {
                log.info("Role {} không phải CUSTOMER -> bỏ qua.", event.role());
                return;
            }
            if (event.userId() == null) {
                log.warn("Event onboarding thiếu userId -> bỏ qua.");
                return;
            }

            CreateCustomerDraftCommand command = new CreateCustomerDraftCommand(
                    UUID.fromString(event.userId()),
                    event.fullName(),
                    event.email(),
                    event.phoneNumber()
            );
            createCustomerDraftUseCase.execute(command);

        } catch (Exception e) {
            log.error("Lỗi khi xử lý event onboarding cho user {}: {}", event.userId(), e.getMessage(), e);
        }
    }
}
