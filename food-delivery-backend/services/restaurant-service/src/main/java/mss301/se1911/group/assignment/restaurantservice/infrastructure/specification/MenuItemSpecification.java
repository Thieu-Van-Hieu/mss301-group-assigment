package mss301.se1911.group.assignment.restaurantservice.infrastructure.specification;

import jakarta.persistence.criteria.Predicate;
import mss301.se1911.group.assignment.restaurantservice.domain.entity.MenuItemEntity;
import mss301.se1911.group.assignment.restaurantservice.domain.repository.criteria.MenuItemQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MenuItemSpecification {

    public static Specification<MenuItemEntity> getSpecification(MenuItemQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // 1. Lọc theo nhà hàng
            if (criteria.restaurantId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("restaurantId"), criteria.restaurantId()));
            }

            // 2. Tìm theo tên món (LIKE, không phân biệt hoa thường)
            if (criteria.name() != null && !criteria.name().isBlank()) {
                String pattern = "%" + criteria.name().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern));
            }

            // 3. Lọc theo phân loại (category)
            if (criteria.categoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), criteria.categoryId()));
            }

            // 4. Lọc theo khoảng giá
            if (criteria.minPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), criteria.minPrice()));
            }
            if (criteria.maxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.maxPrice()));
            }

            // 5. Lọc theo tình trạng còn phục vụ hay không
            if (criteria.available() != null) {
                predicates.add(criteriaBuilder.equal(root.get("available"), criteria.available()));
            }

            query.orderBy(criteriaBuilder.asc(root.get("name")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
