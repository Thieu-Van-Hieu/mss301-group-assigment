package mss301.se1911.group.assignment.deliveryservice.infrastructure.specification;

import jakarta.persistence.criteria.Predicate;
import mss301.se1911.group.assignment.deliveryservice.domain.entity.DeliveryEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria.DeliveryQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DeliverySpecification {
    public static Specification<DeliveryEntity> getSpecification(DeliveryQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // 1. Lọc theo DriverId (Bắt buộc phải join bảng lấy ID)
            if (criteria.driverId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("driver").get("driverId"), criteria.driverId()));
            }

            // 2. Lọc danh sách Status (Dùng toán tử IN)
            if (criteria.statuses() != null && !criteria.statuses().isEmpty()) {
                predicates.add(root.get("status").in(criteria.statuses()));
            }

            // 3. Lọc theo khoảng thời gian tạo đơn
            if (criteria.fromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), criteria.fromDate()));
            }
            if (criteria.toDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), criteria.toDate()));
            }

            // [QUAN TRỌNG] Sắp xếp theo ngày tạo giảm dần để phục vụ Infinite Scroll trên UI
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
