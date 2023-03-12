package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;

import java.io.Serializable;

/**
 * A DTO for the {@link Ticket} entity
 */
public record TicketBasicDto(String notes, String code, double totalCost, Long clientId, TicketStatus status,
                             TicketPaymentStatus paymentStatus) implements Serializable {
}