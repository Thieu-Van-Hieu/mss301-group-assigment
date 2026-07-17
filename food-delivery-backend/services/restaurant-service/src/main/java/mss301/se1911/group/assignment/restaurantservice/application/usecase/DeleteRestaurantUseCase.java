package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteRestaurantUseCase {

    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void execute(UUID restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new IllegalArgumentException("Không tìm thấy nhà hàng với ID: " + restaurantId);
        }
        // Categories & menu_items được cấu hình ON DELETE CASCADE ở DB nên sẽ tự động dọn dẹp
        restaurantRepository.deleteById(restaurantId);
    }
}
