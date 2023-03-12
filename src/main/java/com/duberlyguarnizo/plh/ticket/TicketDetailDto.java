package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link Ticket} entity
 */
@Builder
public record TicketDetailDto(String notes, String code, List<Long> shipmentIds, double totalCost,
                              Long clientId, Long userId, TicketStatus status,
                              TicketPaymentStatus paymentStatus) implements Serializable {
}