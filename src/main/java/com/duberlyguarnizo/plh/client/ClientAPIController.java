package com.duberlyguarnizo.plh.client;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v1/clients")
public class ClientAPIController {
    private final ClientService service;

    @Autowired
    public ClientAPIController(ClientService service) {
        this.service = service;
    }

    @GetMapping("{idNumber}")
    public ResponseEntity<ClientDto> getClient(@PathVariable String idNumber) {
        var user = service.getClientByIdNumber(idNumber);
        return user.map(clientDto -> new ResponseEntity<>(clientDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<ClientBasicDto>> getWithFilters(@RequestParam(defaultValue = "") String name,
                                                               @RequestParam(defaultValue = "all") String type,
                                                               @RequestParam(defaultValue = "all") String status,
                                                               @RequestParam @DateTimeFormat(pattern = "dd-MM-yy") LocalDate start,
                                                               @RequestParam @DateTimeFormat(pattern = "dd-MM-yy") LocalDate end,
                                                               @RequestParam(defaultValue = "names") String sort,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(sort));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);
        if (start != null && end != null) {
            startDate = start;
            endDate = end;
        }
        var result = service.getAll(name, type, status, startDate, endDate, paging);
        if (result.getContent().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(result.getContent(), HttpStatus.OK);
        }
    }

    @PostMapping
    public ResponseEntity<Boolean> create(@Valid @RequestBody ClientDetailDto dto) {
        var result = service.save(dto);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping
    public ResponseEntity<Boolean> update(@Valid @RequestBody ClientDetailDto dto) {
        var user = service.getBasicClientByIdNumber(dto.idNumber());
        if (user.isEmpty()) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);
        }
        var result = service.save(dto);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("{idNumber}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Boolean> delete(@PathVariable String idNumber) {
        var user = service.getBasicClientByIdNumber(idNumber);
        if (user.isEmpty()) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);
        } else {
            var result = service.delete(idNumber);
            return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
