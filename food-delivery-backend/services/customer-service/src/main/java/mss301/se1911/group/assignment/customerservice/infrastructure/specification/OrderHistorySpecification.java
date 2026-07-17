package mss301.se1911.group.assignment.customerservice.infrastructure.specification;

import jakarta.persistence.criteria.Predicate;
import mss301.se1911.group.assignment.customerservice.domain.entity.OrderHistoryEntity;
import mss301.se1911.group.assignment.customerservice.domain.repository.criteria.OrderHistoryQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderHistorySpecification {

    public static Specification<OrderHistoryEntity> getSpecification(OrderHistoryQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria == null) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            if (criteria.customerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customerId"), criteria.customerId()));
            }

            if (criteria.status() != null && !criteria.status().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.status()));
            }

            // Đơn mới nhất lên đầu
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
