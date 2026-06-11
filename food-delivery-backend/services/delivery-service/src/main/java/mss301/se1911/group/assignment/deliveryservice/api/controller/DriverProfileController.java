package mss301.se1911.group.assignment.deliveryservice.api.controller;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.api.dto.request.CompleteOnboardingRequest;
import mss301.se1911.group.assignment.deliveryservice.api.dto.response.DriverStatusCheckResponse;
import mss301.se1911.group.assignment.deliveryservice.application.command.CompleteDriverOnboardingCommand;
import mss301.se1911.group.assignment.deliveryservice.application.dto.DriverSelfProfileResponse;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.CompleteDriverOnboardingUseCase;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.DeactivateDriverSelfUseCase;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.GetDriverSelfProfileUseCase;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DriverStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverProfileController {

    private final CompleteDriverOnboardingUseCase completeDriverOnboardingUseCase;
    private final GetDriverSelfProfileUseCase getDriverSelfProfileUseCase;
    private final DeactivateDriverSelfUseCase deactivateDriverSelfUseCase;

    @GetMapping("/me/status")
    public ResponseEntity<DriverStatusCheckResponse> checkLoginStatus(@RequestHeader("X-User-Id") UUID driverId) {
        DriverSelfProfileResponse profile = getDriverSelfProfileUseCase.execute(driverId);

        // Nếu trạng thái là PENDING_ONBOARDING nghĩa là mới đồng bộ từ Keycloak qua, chưa điền thông tin xe
        boolean requireOnboarding = (profile.status() == DriverStatus.PENDING_ONBOARDING);

        String message = requireOnboarding
                ? "Tài xế cần hoàn thiện thông tin xe và bằng lái trước khi sử dụng hệ thống!"
                : "Hồ sơ hợp lệ, chào mừng bạn quay trở lại!";

        return ResponseEntity.ok(new DriverStatusCheckResponse(
                driverId.toString(),
                profile.status(),
                requireOnboarding,
                message
        ));
    }

    @PutMapping("/me/onboard")
    public ResponseEntity<String> completeOnboarding(
            @RequestHeader("X-User-Id") UUID driverId,
            @RequestBody CompleteOnboardingRequest request) {

        // Ánh xạ dữ liệu từ Presentation Request sang Application Command
        CompleteDriverOnboardingCommand command = new CompleteDriverOnboardingCommand(
                driverId,
                request.identityNumber(),
                request.licenseNumber(),
                request.licensePlate(),
                request.vehicleType(),
                request.vehicleColor()
        );

        completeDriverOnboardingUseCase.execute(command);
        return ResponseEntity.ok("Kích hoạt tài khoản tài xế thành công! Bạn đang ở trạng thái AVAILABLE.");
    }

    @GetMapping("/me")
    public ResponseEntity<DriverSelfProfileResponse> getMyProfile(@RequestHeader("X-User-Id") UUID driverId) {
        DriverSelfProfileResponse response = getDriverSelfProfileUseCase.execute(driverId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMyAccount(@RequestHeader("X-User-Id") UUID driverId) {
        deactivateDriverSelfUseCase.execute(driverId);
        return ResponseEntity.ok("Tài khoản của bạn đã được đóng thành công. Hẹn gặp lại!");
    }
}
