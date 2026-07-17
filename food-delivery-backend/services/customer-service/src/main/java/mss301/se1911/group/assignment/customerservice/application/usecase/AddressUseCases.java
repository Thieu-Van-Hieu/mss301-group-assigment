package mss301.se1911.group.assignment.customerservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.customerservice.application.command.CreateAddressCommand;
import mss301.se1911.group.assignment.customerservice.application.command.UpdateAddressCommand;
import mss301.se1911.group.assignment.customerservice.application.dto.AddressResponse;
import mss301.se1911.group.assignment.customerservice.domain.aggregate.AddressAggregate;
import mss301.se1911.group.assignment.customerservice.domain.repository.AddressRepository;
import mss301.se1911.group.assignment.customerservice.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressUseCases {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public AddressResponse add(CreateAddressCommand command) {
        if (!customerRepository.existsById(command.customerId())) {
            throw new IllegalArgumentException("Không tìm thấy khách hàng với ID: " + command.customerId());
        }

        // Nếu đặt làm mặc định thì gỡ cờ mặc định của các địa chỉ cũ trước
        if (command.isDefault()) {
            addressRepository.clearDefaultForCustomer(command.customerId());
        }

        AddressAggregate aggregate = AddressAggregate.createNew(
                command.customerId(), command.recipientName(), command.phoneNumber(), command.addressLine(),
                command.ward(), command.district(), command.city(),
                command.latitude(), command.longitude(), command.isDefault());

        addressRepository.save(aggregate);
        return AddressResponse.fromAggregate(aggregate);
    }

    @Transactional
    public AddressResponse update(UpdateAddressCommand command) {
        AddressAggregate aggregate = addressRepository.findById(command.addressId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ với ID: " + command.addressId()));

        aggregate.update(
                command.recipientName(), command.phoneNumber(), command.addressLine(),
                command.ward(), command.district(), command.city(),
                command.latitude(), command.longitude());

        addressRepository.save(aggregate);
        return AddressResponse.fromAggregate(aggregate);
    }

    @Transactional
    public void delete(UUID addressId) {
        addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ với ID: " + addressId));
        addressRepository.deleteById(addressId);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> listByCustomer(UUID customerId) {
        return addressRepository.findByCustomerId(customerId).stream()
                .map(AddressResponse::fromAggregate)
                .toList();
    }

    @Transactional
    public AddressResponse setDefault(UUID addressId) {
        AddressAggregate aggregate = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy địa chỉ với ID: " + addressId));

        // Gỡ cờ mặc định của toàn bộ địa chỉ của khách rồi đặt địa chỉ này làm mặc định
        addressRepository.clearDefaultForCustomer(aggregate.getRootEntity().getCustomerId());
        aggregate.markAsDefault();
        addressRepository.save(aggregate);

        return AddressResponse.fromAggregate(aggregate);
    }
}
