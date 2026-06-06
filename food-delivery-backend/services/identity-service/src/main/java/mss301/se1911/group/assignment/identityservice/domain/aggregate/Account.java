package mss301.se1911.group.assignment.identityservice.domain.aggregate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mss301.se1911.group.assignment.identityservice.domain.vo.AccountId;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {
    private AccountId id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String role;
    private boolean enabled;

    public static Account create(String fullName, String phoneNumber, String email, String role) {
        Account account = new Account();
        account.fullName = fullName;
        account.phoneNumber = phoneNumber;
        account.email = email;
        account.role = role;
        account.enabled = true;
        return account;
    }

    public void assignId(AccountId id) {
        this.id = id;
    }
}