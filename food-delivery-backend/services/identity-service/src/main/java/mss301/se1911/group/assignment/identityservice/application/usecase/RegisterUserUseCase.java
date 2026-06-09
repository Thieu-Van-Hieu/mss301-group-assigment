package mss301.se1911.group.assignment.identityservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.commonevents.identity.UserCreatedEvent;
import mss301.se1911.group.assignment.identityservice.application.command.RegisterUserCommand;
import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityEventPublisher;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityRepository;
import mss301.se1911.group.assignment.identityservice.domain.vo.AccountId;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final IdentityRepository identityRepository;
    private final IdentityEventPublisher eventPublisher;

    public Account execute(RegisterUserCommand command) {
        // 1. Tạo Domain Object
        Account newAccount = Account.create(command.fullName(), command.phoneNumber(), command.email(), command.role());

        // 2. Gọi Adapter qua Port
        String externalId = identityRepository.create(newAccount, command.password());

        // 3. Định danh Aggregate
        newAccount.assignId(AccountId.of(externalId));

        // 4. 
        // 2. Tạo Domain Event với các thông tin tối giản chuẩn chỉnh
        UserCreatedEvent event = UserCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .userId(newAccount.getId().value().toString())
                .fullName(newAccount.getFullName())
                .email(newAccount.getEmail())
                .phoneNumber(newAccount.getPhoneNumber())
                .role(newAccount.getRole())
                .build();

        // 3. Phát sự kiện ra thế giới bên ngoài thông qua Port
        eventPublisher.publishUserCreated(event);

        return newAccount;
    }
}