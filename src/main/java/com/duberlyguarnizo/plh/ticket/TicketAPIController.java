package com.duberlyguarnizo.plh.ticket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping
    public ResponseEntity<Map<String, String>> createTicket(@RequestBody TicketDetailDto dto) {
        var result = service.create(dto);
        return result ? new ResponseEntity<>(Map.of("code", "code"), HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
