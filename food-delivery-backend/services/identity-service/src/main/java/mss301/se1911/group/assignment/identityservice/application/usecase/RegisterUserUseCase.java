package mss301.se1911.group.assignment.identityservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.identityservice.application.command.RegisterUserCommand;
import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;
import mss301.se1911.group.assignment.identityservice.domain.event.UserCreatedEvent;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityEventPublisher;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityRepository;
import mss301.se1911.group.assignment.identityservice.domain.vo.AccountId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final IdentityRepository identityRepository;
    private final IdentityEventPublisher eventPublisher;

    public Account execute(RegisterUserCommand command) {
        // 1. Tạo Domain Object
        Account newAccount = Account.create(command.username(), command.email(), command.role());

        // 2. Gọi Adapter qua Port
        String externalId = identityRepository.create(newAccount, command.password());

        // 3. Định danh Aggregate
        newAccount.assignId(AccountId.of(externalId));

        // 4. 
        // 2. Tạo Domain Event với các thông tin tối giản chuẩn chỉnh
        UserCreatedEvent event = new UserCreatedEvent(
                newAccount.getId().value().toString(),
                newAccount.getUsername(),
                newAccount.getEmail(),
                newAccount.getRole()
        );

        // 3. Phát sự kiện ra thế giới bên ngoài thông qua Port
        eventPublisher.publishUserCreated(event);

        return newAccount;
    }
}