package com.duberlyguarnizo.plh.client;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ClientBasicDto>> getWithFilters(@RequestParam(defaultValue = "all") String type,
                                                               @RequestParam(defaultValue = "all") String status,
                                                               @RequestParam(defaultValue = "") String query,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        var result = service.getWithFilters(type, status, query, page, size);
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
