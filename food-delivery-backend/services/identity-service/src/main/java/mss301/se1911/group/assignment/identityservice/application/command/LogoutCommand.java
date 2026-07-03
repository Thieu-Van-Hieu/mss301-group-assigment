package mss301.se1911.group.assignment.identityservice.application.command;

import lombok.Builder;

@Builder
public record LogoutCommand(String refreshToken) {
}
