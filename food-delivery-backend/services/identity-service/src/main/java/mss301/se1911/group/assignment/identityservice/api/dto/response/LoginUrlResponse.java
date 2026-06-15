package mss301.se1911.group.assignment.identityservice.api.dto.response;

import lombok.Builder;

@Builder
public record LoginUrlResponse(String loginUrl, String state) {
}
