package mss301.se1911.group.assignment.identityservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.commonsecurity.dto.response.TokenResponse;
import mss301.se1911.group.assignment.identityservice.application.query.ExchangeCodeQuery;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeCodeUseCase {
    private final IdentityRepository identityRepository;

    public TokenResponse execute(ExchangeCodeQuery command) {
        return identityRepository.exchangeCode(command.code(), command.redirectUri());
    }
}
