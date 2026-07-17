package mss301.se1911.group.assignment.customerservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.customerservice.application.command.CreateCustomerDraftCommand;
import mss301.se1911.group.assignment.customerservice.domain.aggregate.CustomerAggregate;
import mss301.se1911.group.assignment.customerservice.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateCustomerDraftUseCase {

    private final CustomerRepository customerRepository;

    @Transactional
    public void execute(CreateCustomerDraftCommand command) {
        // Idempotent: nếu khách hàng đã tồn tại (nhận trùng event) thì bỏ qua
        if (customerRepository.existsById(command.userId())) {
            log.info("Khách hàng {} đã tồn tại, bỏ qua tạo hồ sơ nháp.", command.userId());
            return;
        }

        CustomerAggregate aggregate = CustomerAggregate.createDraft(
                command.userId(), command.fullName(), command.email(), command.phoneNumber());
        customerRepository.save(aggregate);
        log.info("Đã tạo hồ sơ khách hàng cho userId: {}", command.userId());
    }
}
