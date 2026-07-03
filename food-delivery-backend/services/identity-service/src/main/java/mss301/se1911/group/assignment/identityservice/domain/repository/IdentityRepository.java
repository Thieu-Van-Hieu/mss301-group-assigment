package mss301.se1911.group.assignment.identityservice.domain.repository;

import mss301.se1911.group.assignment.commonsecurity.dto.response.TokenResponse;
import mss301.se1911.group.assignment.commonsecurity.dto.response.UserValidateResponse;
import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;

public interface IdentityRepository {
    String create(Account account, String rawPassword);

    TokenResponse exchangeCode(String code, String redirectUri);

    UserValidateResponse introspectToken(String token);

    TokenResponse refreshToken(String refreshToken);

    void logout(String refreshToken);
}