package mss301.se1911.group.assignment.deliveryservice.application.usecase;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.application.command.CreateDriverDraftCommand;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class CreateDriverDraftProfileUseCase {

    private final DriverProfileRepository driverProfileRepository;

    @Transactional
    public void execute(CreateDriverDraftCommand command) {
        // Kiểm tra Id tài xế đã tồn tại trong hệ thống chưa để tránh ghi đè dữ liệu xấu
        driverProfileRepository.findById(command.driverId()).ifPresent(driver -> {
            throw new IllegalStateException("Hồ sơ tài xế với ID này đã tồn tại!");
        });

        // Tạo Entity dạng bản nháp (Draft)
        DriverProfileEntity draftEntity = DriverProfileEntity.builder()
                .driverId(command.driverId())
                .fullName(command.fullName())
                .phoneNumber(command.phoneNumber())
                .email(command.email())
                .status(DriverStatus.PENDING_ONBOARDING) // Trạng thái chờ điền thông tin xe
                .walletBalance(BigDecimal.ZERO)          // Ví ban đầu bằng 0đ
                .online(false)
                .createdAt(ZonedDateTime.now())
                .build();

        // Đóng gói vào Aggregate Root và ra lệnh cho Repo lưu xuống DB Postgres
        DriverProfileAggregate driverAggregate = new DriverProfileAggregate(draftEntity);
        driverProfileRepository.save(driverAggregate);
    }
}
