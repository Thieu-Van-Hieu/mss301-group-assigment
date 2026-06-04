package mss301.se1911.group.assignment.paymentservices.application.command;

import mss301.se1911.group.assignment.commonevents.event.order.OrderCompletedEvent;

public record ProcessPayoutCommand(OrderCompletedEvent event) {
}
