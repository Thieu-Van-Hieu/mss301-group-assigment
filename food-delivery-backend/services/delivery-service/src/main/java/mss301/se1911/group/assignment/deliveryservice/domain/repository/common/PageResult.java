package mss301.se1911.group.assignment.deliveryservice.domain.repository.common;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {}
