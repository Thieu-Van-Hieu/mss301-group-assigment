package mss301.se1911.group.assignment.paymentservices.application.command;

import mss301.se1911.group.assignment.commonevents.OrderCompletedKafkaEvent;

public record ProcessPayoutCommand(OrderCompletedKafkaEvent event) {
}
