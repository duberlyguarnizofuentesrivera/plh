package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.address.AddressBasicDto;
import com.duberlyguarnizo.plh.util.PlhException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientAPIController {
    private final ClientService service;
    private final PagedResourcesAssembler<ClientBasicDto> pagedResourcesAssembler;


    @Autowired
    public ClientAPIController(ClientService service, PagedResourcesAssembler<ClientBasicDto> pagedResourcesAssembler) {
        this.service = service;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
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
    @Cacheable(cacheNames = "listOfClients")
    public ResponseEntity<PagedModel<EntityModel<ClientBasicDto>>> getWithFilters(@RequestParam(defaultValue = "") String search,
                                                                                  @RequestParam(defaultValue = "all") String clientType,
                                                                                  @RequestParam(defaultValue = "all") String status,
                                                                                  @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(7)}") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                                                                  @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
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
            Page<ClientBasicDto> result = service.getAll(search, clientType, status, startDate, endDate, paging).map(client -> client
                    .add(linkTo(methodOn(ClientAPIController.class)
                            .getClient(client.getId()))
                            .withSelfRel())
                    //TODO: implement search ticket by user and replace this
                    .add(linkTo(ClientAPIController.class)
                            .slash("?search=" + client.getNames())
                            .withRel("search")));
            var pagedModel = pagedResourcesAssembler.toModel(result);
            if (result.getContent().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return ResponseEntity.ok()
                        .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                        .body(pagedModel);
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

    @PostMapping("/{id}/add-address")
    public ResponseEntity<Boolean> addSingleAddress(@PathVariable Long id, @Valid @RequestBody AddressBasicDto addressBasicDto) {
        try {
            var result = service.saveSingleAddress(id, addressBasicDto);
            if (result) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @DeleteMapping("{clientId}/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> addSingleAddress(@PathVariable Long clientId, @PathVariable Long addressId) {
        try {
            var result = service.deleteSingleAddress(clientId, addressId);
            if (result) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
