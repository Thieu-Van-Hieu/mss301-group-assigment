package mss301.se1911.group.assignment.deliveryservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.VehicleType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class DriverProfileAggregate {

    private final DriverProfileEntity rootEntity;
    private static final BigDecimal MIN_WALLET_THRESHOLD = new BigDecimal("100000.00");

    /**
     * Nghiệp vụ Onboarding: Cập nhật thông tin chi tiết lần đầu để kích hoạt tài xế
     */
    public void completeOnboarding(String identityNumber, String licenseNumber,
                                   String licensePlate, VehicleType vehicleType, String vehicleColor) {
        if (this.rootEntity.getStatus() != DriverStatus.PENDING_ONBOARDING) {
            throw new IllegalStateException("Tài xế đã hoàn thành onboarding trước đó hoặc tài khoản không hợp lệ!");
        }

        this.rootEntity.setIdentityNumber(identityNumber);
        this.rootEntity.setLicenseNumber(licenseNumber);
        this.rootEntity.setLicensePlate(licensePlate);
        this.rootEntity.setVehicleType(vehicleType);
        this.rootEntity.setVehicleColor(vehicleColor);
        this.rootEntity.setStatus(DriverStatus.AVAILABLE);
        this.rootEntity.setWalletBalance(BigDecimal.ZERO); // Khởi tạo ví bằng 0
    }

    /**
     * Nghiệp vụ Bật/Tắt trạng thái Online (Áp dụng Luật ví ký quỹ tối thiểu)
     */
    public void toggleOnlineStatus(boolean turnOnline) {
        if (this.rootEntity.getStatus() != DriverStatus.AVAILABLE) {
            throw new IllegalStateException("Tài khoản tài xế chưa được kích hoạt hoặc đã bị khóa!");
        }

        if (turnOnline) {
            // LUẬT: Check ví ký quỹ tối thiểu 100k
            if (this.rootEntity.getWalletBalance() == null ||
                    this.rootEntity.getWalletBalance().compareTo(MIN_WALLET_THRESHOLD) < 0) {
                throw new IllegalStateException("Số dư ví không đủ tối thiểu 100,000 VND để bật chế độ nhận đơn!");
            }
            this.rootEntity.setOnline(true);
        } else {
            this.rootEntity.setOnline(false);
        }
    }

    /**
     * Nghiệp vụ cộng/trừ tiền trong ví (Dùng khi trả thu hộ COD hoặc nhận hoa hồng)
     */
    public void adjustWalletBalance(BigDecimal amount) {
        BigDecimal currentBalance = this.rootEntity.getWalletBalance() != null ? this.rootEntity.getWalletBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.add(amount);
        this.rootEntity.setWalletBalance(newBalance);

        // Nếu sau khi trừ tiền tài chính, ví tụt dưới 100k -> Tự động đá offline luôn để bảo vệ hệ thống
        if (newBalance.compareTo(MIN_WALLET_THRESHOLD) < 0 && this.rootEntity.isOnline()) {
            this.rootEntity.setOnline(false);
        }
    }

    /**
     * Nghiệp vụ Xóa mềm (Soft Delete)
     */
    public void deactivate() {
        this.rootEntity.setStatus(DriverStatus.DEACTIVATED);
        this.rootEntity.setOnline(false);
        this.rootEntity.setDeletedAt(ZonedDateTime.now());
    }
}
