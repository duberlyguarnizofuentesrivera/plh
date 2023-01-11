package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCode(String ticketCode);

    List<Ticket> findByClient_Id(Long clientId);//NOSONAR

    List<Ticket> findByClient_IdNumber(String clientIdNumber);//NOSONAR

    List<Ticket> findByUser_Id(Long systemUserId, PageRequest pageRequest);//NOSONAR

    List<Ticket> findByStatus(TicketStatus ticketStatus, PageRequest pageRequest);

    List<Ticket> findByPaymentStatus(TicketPaymentStatus ticketPaymentStatus, PageRequest pageRequest);

    List<Ticket> findByModificationDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

}
