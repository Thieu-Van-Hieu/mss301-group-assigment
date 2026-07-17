package mss301.se1911.group.assignment.restaurantservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.restaurantservice.application.dto.RestaurantResponse;
import mss301.se1911.group.assignment.restaurantservice.domain.aggregate.RestaurantAggregate;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.RestaurantRepository;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.common.PageResult;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.RestaurantQueryCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantQueryUseCases {

    private final RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public RestaurantResponse getById(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(RestaurantResponse::fromAggregate)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà hàng với ID: " + restaurantId));
    }

    @Transactional(readOnly = true)
    public PageResult<RestaurantResponse> search(RestaurantQueryCriteria criteria, int page, int size) {
        PageResult<RestaurantAggregate> raw = restaurantRepository.findAllWithFilter(criteria, page, size);

        List<RestaurantResponse> content = raw.content().stream()
                .map(RestaurantResponse::fromAggregate)
                .toList();

        return new PageResult<>(
                content,
                raw.totalElements(),
                raw.totalPages(),
                raw.pageNumber(),
                raw.pageSize()
        );
    }
}
