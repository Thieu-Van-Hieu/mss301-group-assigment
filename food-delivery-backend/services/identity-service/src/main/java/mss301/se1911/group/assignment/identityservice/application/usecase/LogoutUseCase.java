package mss301.se1911.group.assignment.identityservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.identityservice.application.command.LogoutCommand;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {
    private final IdentityRepository identityRepository;

    public void execute(LogoutCommand command) {
        identityRepository.logout(command.refreshToken());
    }
}
