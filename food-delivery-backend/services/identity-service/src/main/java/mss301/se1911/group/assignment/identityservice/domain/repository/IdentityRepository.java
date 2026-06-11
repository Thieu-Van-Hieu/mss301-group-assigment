package mss301.se1911.group.assignment.identityservice.domain.repository;

import mss301.se1911.group.assignment.identityservice.domain.aggregate.Account;

public interface IdentityRepository {
    String create(Account account, String rawPassword);
}