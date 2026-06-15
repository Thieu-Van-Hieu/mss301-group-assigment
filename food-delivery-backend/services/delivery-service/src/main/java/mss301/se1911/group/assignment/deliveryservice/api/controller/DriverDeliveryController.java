package mss301.se1911.group.assignment.deliveryservice.api.controller;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.deliveryservice.api.dto.request.CancelDeliveryRequest;
import mss301.se1911.group.assignment.deliveryservice.api.dto.response.DeliveryWebResponse;
import mss301.se1911.group.assignment.deliveryservice.application.dto.DeliveryResponse;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.AcceptDeliveryUseCase;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.CancelDeliveryUseCases;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.DeliveryQueryUseCases;
import mss301.se1911.group.assignment.deliveryservice.application.usecase.UpdateDeliveryProgressUseCase;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria.DeliveryQueryCriteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers/me/deliveries")
@RequiredArgsConstructor
public class DriverDeliveryController {

    private final AcceptDeliveryUseCase acceptDeliveryUseCase;
    private final UpdateDeliveryProgressUseCase updateDeliveryProgressUseCase;
    private final DeliveryQueryUseCases deliveryQueryUseCases;
    private final CancelDeliveryUseCases cancelDeliveryUseCases;

    @GetMapping("/active")
    public ResponseEntity<DeliveryWebResponse> getActiveDelivery(@RequestHeader("X-User-Id") UUID driverId) {
        DeliveryResponse appDto = deliveryQueryUseCases.getActiveDelivery(driverId);

        if (appDto == null) {
            return ResponseEntity.noContent().build(); // Trả về 204 nếu tài xế không có đơn
        }

        return ResponseEntity.ok(DeliveryWebResponse.fromAppDto(appDto));
    }

    @GetMapping("/history")
    public ResponseEntity<PageResult<DeliveryWebResponse>> getHistory(
            @RequestHeader("X-User-Id") UUID driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        DeliveryQueryCriteria criteria = new DeliveryQueryCriteria(driverId, null, null, null);
        PageResult<DeliveryResponse> appPageResult = deliveryQueryUseCases.getDriverDeliveryHistory(criteria, page, size);

        // Map List<Application DTO> sang List<Web Response>
        List<DeliveryWebResponse> webResponses = appPageResult.content().stream()
                .map(DeliveryWebResponse::fromAppDto)
                .toList();

        // Đóng gói lại vào PageResult (Có thể tạo thêm một class PageWebResponse nếu muốn tách bạch hoàn toàn)
        PageResult<DeliveryWebResponse> finalResponse = new PageResult<>(
                webResponses, appPageResult.totalElements(), appPageResult.totalPages(),
                appPageResult.pageNumber(), appPageResult.pageSize()
        );

        return ResponseEntity.ok(finalResponse);
    }

    @PostMapping("/{deliveryId}/accept")
    public ResponseEntity<String> acceptDelivery(
            @RequestHeader("X-User-Id") UUID driverId,
            @PathVariable UUID deliveryId) {
        acceptDeliveryUseCase.execute(deliveryId, driverId);
        return ResponseEntity.ok("Nhận đơn thành công! Hãy di chuyển đến nhà hàng.");
    }

    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<String> updateStatus(
            @RequestHeader("X-User-Id") UUID driverId,
            @PathVariable UUID deliveryId,
            @RequestParam DeliveryStatus status) {
        updateDeliveryProgressUseCase.execute(deliveryId, driverId, status);
        return ResponseEntity.ok("Cập nhật trạng thái chuyến đi thành công!");
    }

    @PostMapping("/{deliveryId}/cancel")
    public ResponseEntity<String> cancelDelivery(
            @RequestHeader("X-User-Id") UUID driverId,
            @PathVariable UUID deliveryId,
            @RequestBody CancelDeliveryRequest request) { // Dùng Web Request thay vì truyền String trơn

        cancelDeliveryUseCases.cancelByDriver(deliveryId, driverId, request.reason());
        return ResponseEntity.ok("Đã ghi nhận sự cố. Bộ phận hỗ trợ sẽ liên hệ bạn sớm nhất!");
    }
}