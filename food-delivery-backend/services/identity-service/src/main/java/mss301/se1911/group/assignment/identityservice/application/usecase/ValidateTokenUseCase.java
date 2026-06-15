package mss301.se1911.group.assignment.identityservice.application.usecase;

import lombok.RequiredArgsConstructor;
import mss301.se1911.group.assignment.commonsecurity.dto.response.UserValidateResponse;
import mss301.se1911.group.assignment.identityservice.application.query.ValidateTokenQuery;
import mss301.se1911.group.assignment.identityservice.domain.repository.IdentityRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateTokenUseCase {

    private final IdentityRepository identityRepository;

    public UserValidateResponse execute(ValidateTokenQuery query) {
        return identityRepository.introspectToken(query.accessToken());
    }
}