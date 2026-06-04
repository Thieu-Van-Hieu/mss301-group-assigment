package mss301.se1911.group.assignment.paymentservices.application.command;

import java.util.Map;

public record ProcessVnPayCallbackCommand(Map<String, String> requestParams) {
}
