package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.duberlyguarnizo.plh.ticket.TicketRepository.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Slf4j
public class TicketService {
    private final TicketRepository repository;
    private final TicketMapper mapper = Mappers.getMapper(TicketMapper.class);

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public Optional<TicketDto> getTicket(String code) {
        var repoTicket = repository.findByCode(code);
        return repoTicket.map(mapper::toDto);
    }

    public Optional<TicketDetailDto> getDetailTicket(String code) {
        var repoTicket = repository.findByCode(code);
        return repoTicket.map(mapper::toDetailDto);
    }

    public Page<TicketBasicDto> getWIthFilters(String userFirstName,
                                               String clientName,
                                               String ticketStatus,
                                               String paymentStatus,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               PageRequest paging) {
        TicketPaymentStatus paymentStatusValue;
        TicketStatus statusValue;
        try {
            paymentStatusValue = TicketPaymentStatus.valueOf(paymentStatus);
        } catch (Exception e) {
            paymentStatusValue = null;
        }
        try {
            statusValue = TicketStatus.valueOf(ticketStatus);
        } catch (Exception e) {
            statusValue = null;
        }
        if (paging == null) {
            paging = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
        }
        try {
            return repository.findAll(where(hasStatus(statusValue))
                            .and(hasPaymentStatus(paymentStatusValue))
                            .and(hasUserName(userFirstName))
                            .and(hasClientName(clientName))
                            .and(dateIsBetween(startDate, endDate)), paging)
                    .map(mapper::toBasicDto);
        } catch (Exception e) {
            log.error("TicketService: getWithFilters(): Could not query the repository. Error: {}", e.getMessage());
            return Page.empty();
        }
    }

    public boolean create(TicketDetailDto dto) {
        try {
            String generatedCode = generateCode(dto.clientId());
            Ticket ticket = mapper.toEntity(dto);
            ticket.setCode(generatedCode);
            repository.save(ticket);
            return true;
        } catch (Exception e) {
            log.error("TicketService: save(): Failed to save ticket detail entity with code {}. Error: {}", dto.code(), e.getMessage());
            return false;
        }
    }

    public boolean update(TicketDetailDto dto) {
        var repoTicket = repository.findByCode(dto.code());
        if (repoTicket.isPresent()) {
            try {
                repository.save(mapper.partialUpdate(dto, repoTicket.get()));
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }

    }

    public boolean delete(String code) {
        var repoTicket = repository.findByCode(code);
        if (repoTicket.isPresent()) {
            try {
                repository.delete(repoTicket.get());
                return true;
            } catch (Exception e) {
                log.error("TicketService: delete(): Error deleting ticket with code {}. Error: {}", code, e.getMessage());
                return false;
            }
        } else {
            log.info("TicketService: delete(): Ticket with code {} does not exits. Cannot delete", code);
            return false;
        }
    }

    private String generateCode(Long clientId) {
        var diff = Duration.between(LocalDate.of(2020, 1, 1).atStartOfDay(),
                LocalDateTime.now());
        return Long.toHexString(clientId) + "-" + Long.toHexString(diff.toSeconds());
    }
}
