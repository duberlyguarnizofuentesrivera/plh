package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A DTO for the {@link Ticket} entity
 */
public record TicketDto(Long createdBy, LocalDateTime createdDate, Long lastModifiedBy, LocalDateTime lastModifiedDate,
                        String notes, Long id, String code, List<Long> shipmentIds, double totalCost, Long clientId,
                        Long userId, TicketStatus status, TicketPaymentStatus paymentStatus) implements Serializable {
}