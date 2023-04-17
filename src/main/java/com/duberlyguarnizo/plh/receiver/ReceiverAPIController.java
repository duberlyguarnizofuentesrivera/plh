package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.client.ClientAPIController;
import com.duberlyguarnizo.plh.util.PlhException;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/receivers")
public class ReceiverAPIController {
    private final ReceiverService service;
    private final PagedResourcesAssembler<ReceiverBasicDto> pagedResourcesAssembler;

    public ReceiverAPIController(ReceiverService service, PagedResourcesAssembler<ReceiverBasicDto> pagedResourcesAssembler) {
        this.service = service;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping("{idNumber}")
    public ResponseEntity<ReceiverDto> getReceiver(@PathVariable String idNumber) {
        var result = service.getReceiver(idNumber);
        return result.map(receiverDto -> new ResponseEntity<>(receiverDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("{id}")
    public ResponseEntity<ReceiverDto> getReceiver(@PathVariable long id) {
        var result = service.getReceiverById(id);
        return result.map(receiverDto -> new ResponseEntity<>(receiverDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ReceiverBasicDto>>> getWithFilters(@RequestParam(defaultValue = "") String type,
                                                                                    @RequestParam(defaultValue = "") String names,
                                                                                    @RequestParam(defaultValue = "") String idNumber,
                                                                                    @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(7)}") @DateTimeFormat(pattern = "dd-MM-yy") LocalDate start,
                                                                                    @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(pattern = "dd-MM-yy") LocalDate end,
                                                                                    @RequestParam(defaultValue = "names") String sort,
                                                                                    @RequestParam(defaultValue = "1") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) {
        //TODO: verify filtering by idNumber works properly
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(sort));
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        if (start != null && end != null) {
            startDate = start;
            endDate = end;
        }
        try {
            Page<ReceiverBasicDto> result = service.getAll(type, names, idNumber, startDate, endDate, paging).map(receiver -> receiver
                    .add(linkTo(methodOn(ReceiverAPIController.class)
                            .getReceiver(receiver.getId()))
                            .withSelfRel())
                    //TODO: implement search ticket by user and replace this
                    .add(linkTo(ClientAPIController.class)
                            .slash("?search=" + receiver.getNames())
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
    public ResponseEntity<Boolean> createReceiver(@Valid @RequestBody ReceiverDetailDto receiver) {
        boolean result = service.save(receiver);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.CREATED) :
                new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
    }

    @PutMapping
    public ResponseEntity<Boolean> updateReceiver(@Valid @RequestBody ReceiverDetailDto receiver) {
        boolean result = service.update(receiver, false); //Do not create if it doesn't exist... To be implemented
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK) :
                new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("{idNumber}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Boolean> deleteReceiver(@PathVariable String idNumber) {
        boolean result = service.delete(idNumber);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK) :
                new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
