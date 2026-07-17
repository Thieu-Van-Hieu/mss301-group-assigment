package mss301.se1911.group.assignment.restaurantservice.domain.aggregate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.RestaurantEntity;
import mss301.se1911.group.assignment.restaurantservice.domain.enums.RestaurantStatus;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RestaurantAggregate {

    private final RestaurantEntity rootEntity;

    /**
     * CREATE: Factory Method khởi tạo nhà hàng mới. Mặc định trạng thái CLOSED cho tới khi chủ quán mở cửa.
     */
    public static RestaurantAggregate createNewRestaurant(
            UUID ownerId, String name, String address, String imageUrl,
            String description, String cuisineType,
            LocalTime openingTime, LocalTime closingTime) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên nhà hàng không được để trống!");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Địa chỉ nhà hàng không được để trống!");
        }

        RestaurantEntity entity = RestaurantEntity.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .name(name)
                .address(address)
                .imageUrl(imageUrl)
                .description(description)
                .cuisineType(cuisineType)
                .status(RestaurantStatus.CLOSED)
                .openingTime(openingTime)
                .closingTime(closingTime)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return new RestaurantAggregate(entity);
    }

    /**
     * UPDATE: Cập nhật thông tin nhà hàng (không đổi trạng thái mở/đóng cửa).
     */
    public void updateInfo(String name, String address, String imageUrl,
                           String description, String cuisineType,
                           LocalTime openingTime, LocalTime closingTime) {
        if (name != null && !name.isBlank()) {
            this.rootEntity.setName(name);
        }
        if (address != null && !address.isBlank()) {
            this.rootEntity.setAddress(address);
        }
        this.rootEntity.setImageUrl(imageUrl);
        this.rootEntity.setDescription(description);
        this.rootEntity.setCuisineType(cuisineType);
        this.rootEntity.setOpeningTime(openingTime);
        this.rootEntity.setClosingTime(closingTime);
        this.rootEntity.setUpdatedAt(ZonedDateTime.now());
    }

    /**
     * Đổi trạng thái mở/đóng cửa theo thời gian thực. Trả về trạng thái cũ để UseCase phát sự kiện.
     */
    public RestaurantStatus changeStatus(RestaurantStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Trạng thái mới không hợp lệ!");
        }
        RestaurantStatus oldStatus = this.rootEntity.getStatus();
        this.rootEntity.setStatus(newStatus);
        this.rootEntity.setUpdatedAt(ZonedDateTime.now());
        return oldStatus;
    }

    /**
     * Kiểm tra nhà hàng có đang thực sự phục vụ ở thời điểm hiện tại không:
     * Vừa phải đang bật trạng thái OPEN, vừa phải nằm trong khung giờ hoạt động (nếu có cấu hình giờ).
     */
    public boolean isOpenNow() {
        if (this.rootEntity.getStatus() != RestaurantStatus.OPEN) {
            return false;
        }

        LocalTime opening = this.rootEntity.getOpeningTime();
        LocalTime closing = this.rootEntity.getClosingTime();

        // Không cấu hình giờ hoạt động -> chỉ phụ thuộc vào trạng thái OPEN
        if (opening == null || closing == null) {
            return true;
        }

        LocalTime now = LocalTime.now();
        if (closing.isAfter(opening)) {
            // Khung giờ trong ngày (vd: 08:00 -> 22:00)
            return !now.isBefore(opening) && !now.isAfter(closing);
        }
        // Khung giờ qua đêm (vd: 18:00 -> 02:00 hôm sau)
        return !now.isBefore(opening) || !now.isAfter(closing);
    }
}
