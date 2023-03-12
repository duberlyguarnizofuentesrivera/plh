package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByCode(String ticketCode);

    Page<Ticket> findByClient_IdNumber(String clientIdNumber, Pageable pageable);//NOSONAR

    Page<Ticket> findByUser_IdNumber(String userIdNumber, Pageable pageable);//NOSONAR

    Page<Ticket> findByStatus(TicketStatus ticketStatus, Pageable pageable);

    Page<Ticket> findByPaymentStatus(TicketPaymentStatus ticketPaymentStatus, Pageable pageable);


    Page<Ticket> findByClient_NamesContainsIgnoreCaseAndUser_FirstNameContainsIgnoreCase(
            String userName,
            String clientName,
            Pageable pageable);

    Page<Ticket> findByUser_FirstNameContainsIgnoreCase(
            String userName,
            Pageable pageable);

    Page<Ticket> findByClient_NamesContainsIgnoreCase(
            String userName,
            Pageable pageable);


    Page<Ticket> findByLastModifiedDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

}
