package mss301.se1911.group.assignment.deliveryservice.infrastructure.specification;

import mss301.se1911.group.assignment.deliveryservice.domain.entity.DriverProfileEntity;
import mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria.DriverQueryCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class DriverProfileSpecification {
    public static Specification<DriverProfileEntity> getSpecification(DriverQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            // 1. Lọc theo Status (ACTIVE, PENDING_ONBOARDING, DEACTIVATED)
            if (criteria.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.status()));
            }

            // 2. Lọc theo Trạng thái Online/Offline
            if (criteria.online() != null) {
                predicates.add(criteriaBuilder.equal(root.get("online"), criteria.online()));
            }

            // 3. Tìm kiếm nâng cao: Like theo Tên HOẶC Số điện thoại (Không phân biệt hoa thường)
            if (criteria.searchKeyword() != null && !criteria.searchKeyword().isBlank()) {
                String keywordPattern = "%" + criteria.searchKeyword().toLowerCase() + "%";

                Predicate matchName = criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), keywordPattern);
                Predicate matchPhone = criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), keywordPattern);

                // Kết hợp điều kiện OR: (fullName LIKE %x% OR phoneNumber LIKE %x%)
                predicates.add(criteriaBuilder.or(matchName, matchPhone));
            }

            // Kết hợp tất cả bằng điều kiện AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
