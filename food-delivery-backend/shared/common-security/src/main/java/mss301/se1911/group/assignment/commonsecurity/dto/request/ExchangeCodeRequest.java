package mss301.se1911.group.assignment.commonsecurity.dto.request;

import lombok.Builder;

@Builder
public record ExchangeCodeRequest(String code, String redirectUri) {
}
