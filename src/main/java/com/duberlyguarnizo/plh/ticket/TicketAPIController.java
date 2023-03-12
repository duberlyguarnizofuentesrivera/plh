package com.duberlyguarnizo.plh.ticket;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketAPIController {
    private final TicketService service;

    public TicketAPIController(TicketService service) {
        this.service = service;
    }

    @GetMapping("{code}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable String code) {
        var result = service.getTicket(code);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TicketBasicDto>> getWithFilters(@RequestParam(defaultValue = "") String userFirstName,
                                                               @RequestParam(defaultValue = "") String clientName,
                                                               @RequestParam(defaultValue = "") String ticketStatus,
                                                               @RequestParam(defaultValue = "") String paymentStatus,
                                                               @RequestParam @DateTimeFormat LocalDate startDate,
                                                               @RequestParam @DateTimeFormat LocalDate endDate,
                                                               @RequestParam(defaultValue = "lastModifiedDate") String sort,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().minusDays(7);
        if (startDate != null && endDate != null) {
            start = startDate;
            end = endDate;
        }
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(sort));
        var result = service.getWIthFilters(userFirstName,
                clientName,
                ticketStatus,
                paymentStatus,
                start,
                end,
                paging);
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(result.getContent());
        }
    }

    @PostMapping
    public ResponseEntity<Boolean> createTicket(@RequestBody TicketDetailDto dto) {
        var result = service.create(dto);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("{code}")
    public ResponseEntity<Boolean> updateTicket(@PathVariable String code, @RequestBody TicketDetailDto dto) {
        var result = service.update(dto);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("{code}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Boolean> deleteTicket(@PathVariable String code) {
        var result = service.delete(code);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
