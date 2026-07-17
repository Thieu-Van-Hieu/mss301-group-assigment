package mss301.se1911.group.assignment.customerservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.domain.entity.CustomerEntity;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CustomerAggregate {

    private final CustomerEntity rootEntity;

    /**
     * Factory Method tạo hồ sơ khách hàng nháp khi nhận event đăng ký tài khoản (role CUSTOMER).
     * id chính là userId từ identity-service để đồng bộ danh tính toàn hệ thống.
     */
    public static CustomerAggregate createDraft(UUID userId, String fullName, String email, String phoneNumber) {
        if (userId == null) {
            throw new IllegalArgumentException("userId không được để trống khi tạo hồ sơ khách hàng!");
        }

        CustomerEntity entity = CustomerEntity.builder()
                .id(userId)
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return new CustomerAggregate(entity);
    }

    /**
     * Cập nhật thông tin cá nhân của khách hàng.
     */
    public void updateProfile(String fullName, String email, String phoneNumber) {
        if (fullName != null && !fullName.isBlank()) {
            this.rootEntity.setFullName(fullName);
        }
        if (email != null && !email.isBlank()) {
            this.rootEntity.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.rootEntity.setPhoneNumber(phoneNumber);
        }
        this.rootEntity.setUpdatedAt(ZonedDateTime.now());
    }
}
