package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    static Specification<Ticket> hasStatus(TicketStatus status) {
        if (status != null) {
            return (ticket, query, cb) -> cb.equal(ticket.get("status"), status);
        } else {
            return null;
        }
    }

    static Specification<Ticket> hasPaymentStatus(TicketPaymentStatus paymentStatus) {
        if (paymentStatus != null) {
            return (ticket, query, cb) -> cb.equal(ticket.get("paymentStatus"), paymentStatus);
        } else {
            return null;
        }
    }

    static Specification<Ticket> hasUserName(String username) {
        if (!username.isEmpty()) {
            return (ticket, query, cb) -> cb.like(ticket.get("user").get("firstName"), "%" + username + "%");
        } else {
            return null;
        }
    }

    static Specification<Ticket> hasClientName(String clientName) {
        if (!clientName.isEmpty()) {
            return (ticket, query, cb) -> cb.like(ticket.get("client").get("names"), "%" + clientName + "%");
        } else {
            return null;
        }
    }

    static Specification<Ticket> dateIsBetween(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();
        return (receiver, query, cb) -> cb.between(receiver.get("lastModifiedDate"), start, end);
    }

    Optional<Ticket> findByCode(String ticketCode);

}
