package mss301.se1911.group.assignment.customerservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.application.dto.CustomerResponse;
import mss301.se1911.group.assignment.customerservice.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerQueryUseCase {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID customerId) {
        return customerRepository.findById(customerId)
                .map(CustomerResponse::fromAggregate)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + customerId));
    }
}
