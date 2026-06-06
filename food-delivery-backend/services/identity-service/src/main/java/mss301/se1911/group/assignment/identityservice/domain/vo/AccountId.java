package mss301.se1911.group.assignment.identityservice.domain.vo;

import java.util.UUID;

public record AccountId(UUID value) {
    public static AccountId of(String value) {
        return new AccountId(UUID.fromString(value));
    }
}
