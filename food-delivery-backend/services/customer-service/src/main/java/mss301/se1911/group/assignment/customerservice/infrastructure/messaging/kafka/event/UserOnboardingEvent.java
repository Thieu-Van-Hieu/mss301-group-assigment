package mss301.se1911.group.assignment.customerservice.infrastructure.messaging.kafka.event;

import lombok.Builder;

/**
 * Event tài khoản mới được tạo, phát bởi identity-service lên topic user-onboarding.
 * Chỉ giữ các trường cần thiết; các trường khác trong payload sẽ được bỏ qua.
 */
@Builder
public record UserOnboardingEvent(
        String userId,
        String fullName,
        String email,
        String phoneNumber,
        String role // "DRIVER", "CUSTOMER", "MERCHANT"
) {}
