package mss301.se1911.group.assignment.deliveryservice.application.usecase;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.aggregate.DriverProfileAggregate;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeactivateDriverSelfUseCase {

    private final DriverProfileRepository driverProfileRepository;

    @Transactional
    public void execute(UUID driverId) {
        // 1. Định danh đúng tài xế đang thực hiện thao tác xóa
        DriverProfileAggregate driverAggregate = driverProfileRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Hồ sơ tài xế không tồn tại!"));

        // 2. Kiểm tra điều kiện ràng buộc tài chính trước khi cho đi (Invariant Check nâng cao nếu cần)
        // Ví dụ: Nếu ví đang âm tiền (Tài xế đang nợ hệ thống), không cho phép xóa tài khoản tự do
        if (driverAggregate.getRootEntity().getWalletBalance() != null &&
                driverAggregate.getRootEntity().getWalletBalance().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Bạn đang có khoản nợ chưa hoàn thành với hệ thống. Không thể xóa tài khoản lúc này!");
        }

        // 3. Gọi hàm hành vi nghiệp vụ xóa mềm hạ tầng từ Aggregate Root (đặt online = false, status = DEACTIVATED, set deletedAt)
        driverAggregate.deactivate();

        // 4. Lưu lại cập nhật trạng thái đóng tài khoản vào Postgres
        driverProfileRepository.save(driverAggregate);
    }
}