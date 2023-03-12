package com.duberlyguarnizo.plh.ticket;

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

    public Page<TicketBasicDto> getWithFilters(String userName, String clientName, String sort, int page, int size) {

        PageRequest pagination = PageRequest.of(page - 1, size, Sort.by(sort));

        if (clientName.isEmpty() && userName.isEmpty()) {
            return repository.findAll(pagination).map(mapper::toBasicDto);
        } else if (!clientName.isEmpty() && !userName.isEmpty()) {
            return repository.findByClient_NamesContainsIgnoreCaseAndUser_FirstNameContainsIgnoreCase(clientName,
                            userName,
                            pagination)
                    .map(mapper::toBasicDto);
        } else if (clientName.isEmpty()) {
            return repository.findByUser_FirstNameContainsIgnoreCase(userName, pagination).map(mapper::toBasicDto);
        } else {
            return repository.findByClient_NamesContainsIgnoreCase(clientName, pagination).map(mapper::toBasicDto);
        }
    }

    public Page<TicketBasicDto> findByDate(LocalDate date, String sort, int page, int size) {
        PageRequest pagination = PageRequest.of(page - 1, size, Sort.by(sort));
        return repository.findByLastModifiedDateBetween(date.atStartOfDay(),
                date.plusDays(1).atStartOfDay(), pagination).map(mapper::toBasicDto);
    }

    public Page<TicketBasicDto> findByDateInterval(LocalDate date1, LocalDate date2, String sort, int page, int size) {
        PageRequest pagination = PageRequest.of(page - 1, size, Sort.by(sort));
        return repository.findByLastModifiedDateBetween(date1.atStartOfDay(),
                date2.plusDays(1).atStartOfDay(), pagination).map(mapper::toBasicDto);
    }

    //TODO: implement get by ticket status and payment status

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
