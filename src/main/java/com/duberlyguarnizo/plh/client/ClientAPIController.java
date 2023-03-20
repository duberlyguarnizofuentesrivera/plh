package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.util.PlhException;
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

    @GetMapping("{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable Long id) {
        try {
            var user = service.getClientById(id);
            return user.map(clientDto -> new ResponseEntity<>(clientDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (PlhException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        if (start != null && end != null) {
            startDate = start;
            endDate = end;
        }
        try {
            var result = service.getAll(name, type, status, startDate, endDate, paging);
            if (result.getContent().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(result.getContent(), HttpStatus.OK);
            }
        } catch (PlhException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Boolean> create(@Valid @RequestBody ClientDetailDto dto) {
        var result = service.save(dto);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PatchMapping
    public ResponseEntity<Boolean> update(@Valid @RequestBody ClientDetailDto dto) {
        try {
            var result = service.update(dto);
            return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);
        } catch (PlhException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        try {
            var result = service.delete(id);
            return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK) : new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);
        } catch (PlhException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
