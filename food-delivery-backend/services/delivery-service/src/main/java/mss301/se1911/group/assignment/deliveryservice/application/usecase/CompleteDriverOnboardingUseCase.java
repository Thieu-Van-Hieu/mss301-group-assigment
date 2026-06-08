package mss301.se1911.group.assignment.deliveryservice.application.usecase;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.application.command.CompleteDriverOnboardingCommand;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompleteDriverOnboardingUseCase {

    private final DriverProfileRepository driverProfileRepository;

    @Transactional
    public void execute(CompleteDriverOnboardingCommand command) {
        // 1. Tìm bản ghi nháp đã tạo trước đó từ Kafka
        DriverProfileAggregate driverAggregate = driverProfileRepository.findById(command.driverId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ tài xế nháp, vui lòng liên hệ bộ phận hỗ trợ!"));

        // 2. Thực hiện Validate định dạng dữ liệu đầu vào (Luật nghiệp vụ hệ thống tự động duyệt)
        validateOnboardingData(command);

        // 3. Đẩy dữ liệu xuống Domain Aggregate xử lý đổi trạng thái thành AVAILABLE (theo cập nhật mới của bạn)
        driverAggregate.completeOnboarding(
                command.identityNumber(),
                command.licenseNumber(),
                command.licensePlate(),
                command.vehicleType(),
                command.vehicleColor()
        );

        // 4. Lưu trạng thái mới xuống Database
        driverProfileRepository.save(driverAggregate);
    }

    private void validateOnboardingData(CompleteDriverOnboardingCommand command) {
        if (command.identityNumber() == null || !command.identityNumber().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Số Căn cước công dân không hợp lệ! Phải bao gồm chính xác 12 chữ số.");
        }
        if (command.licenseNumber() == null || command.licenseNumber().isBlank()) {
            throw new IllegalArgumentException("Số Giấy phép lái xe không được để trống!");
        }
        if (command.licensePlate() == null || command.licensePlate().isBlank()) {
            throw new IllegalArgumentException("Biển số xe không được để trống!");
        }
        if (command.vehicleType() == null) {
            throw new IllegalArgumentException("Loại phương tiện di chuyển không hợp lệ!");
        }
    }
}
