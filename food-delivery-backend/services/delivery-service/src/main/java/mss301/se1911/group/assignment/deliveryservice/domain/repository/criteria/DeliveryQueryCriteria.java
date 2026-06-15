package mss301.se1911.group.assignment.deliveryservice.domain.repository.criteria;
import mss301.se1911.group.assignment.deliveryservice.domain.enums.DeliveryStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryQueryCriteria(
        UUID driverId,                  // Lọc đơn của 1 tài xế cụ thể
        List<DeliveryStatus> statuses,  // Lọc theo nhiều trạng thái (vd: [DELIVERED, FAILED])
        ZonedDateTime fromDate,
        ZonedDateTime toDate
) {}
