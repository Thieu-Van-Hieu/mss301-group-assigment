package mss301.se1911.group.assignment.restaurantservice.infrastructure.specification;

import jakarta.persistence.criteria.Predicate;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.RestaurantEntity;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.RestaurantQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSpecification {

    public static Specification<RestaurantEntity> getSpecification(RestaurantQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // 1. Tìm theo tên nhà hàng (LIKE, không phân biệt hoa thường)
            if (criteria.name() != null && !criteria.name().isBlank()) {
                String pattern = "%" + criteria.name().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern));
            }

            // 2. Lọc theo loại ẩm thực
            if (criteria.cuisineType() != null && !criteria.cuisineType().isBlank()) {
                String pattern = "%" + criteria.cuisineType().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("cuisineType")), pattern));
            }

            // 3. Lọc theo trạng thái mở/đóng cửa
            if (criteria.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.status()));
            }

            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
