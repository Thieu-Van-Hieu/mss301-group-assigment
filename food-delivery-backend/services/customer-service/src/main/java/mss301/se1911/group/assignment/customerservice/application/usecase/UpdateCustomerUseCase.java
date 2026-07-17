package mss301.se1911.group.assignment.customerservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.application.command.UpdateCustomerCommand;
import mss301.se1911.group.assignment.customerservice.application.dto.CustomerResponse;
import mss301.se1911.group.assignment.customerservice.domain.aggregate.CustomerAggregate;
import mss301.se1911.group.assignment.customerservice.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse execute(UpdateCustomerCommand command) {
        CustomerAggregate aggregate = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + command.customerId()));

        aggregate.updateProfile(command.fullName(), command.email(), command.phoneNumber());
        customerRepository.save(aggregate);

        return CustomerResponse.fromAggregate(aggregate);
    }
}
