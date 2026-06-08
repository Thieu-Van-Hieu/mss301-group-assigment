package mss301.se1911.group.assignment.deliveryservice.application.usecase;

import mss301.se1911.group.assignment.deliveryservice.application.dto.DriverSelfProfileResponse;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetDriverSelfProfileUseCase {

    private final DriverProfileRepository driverProfileRepository;

    @Transactional(readOnly = true)
    public DriverSelfProfileResponse execute(UUID driverId) {
        // Tìm kiếm tài xế dựa trên Token ID nhận được từ API Gateway/Security
        DriverProfileAggregate driverAggregate = driverProfileRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Hồ sơ tài xế không tồn tại trong hệ thống!"));

        DriverProfileEntity entity = driverAggregate.getRootEntity();

        // Ánh xạ dữ liệu thô sang DTO phản hồi (Đồng thời trả ra walletBalance phục vụ xem số tiền nợ/dư)
        return new DriverSelfProfileResponse(
                entity.getDriverId(),
                entity.getFullName(),
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getIdentityNumber(),
                entity.getLicenseNumber(),
                entity.getLicensePlate(),
                entity.getVehicleType(),
                entity.getVehicleColor(),
                entity.isOnline(),
                entity.getWalletBalance(), // Dữ liệu báo cáo tài chính cá nhân hiện tại
                entity.getStatus()
        );
    }
}
