package com.duberlyguarnizo.plh.receiver;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/receivers")
public class ReceiverAPIController {
    private final ReceiverService service;

    public ReceiverAPIController(ReceiverService service) {
        this.service = service;
    }

    @GetMapping("{idNumber}")
    public ResponseEntity<ReceiverDto> getReceiver(@PathVariable String idNumber) {
        var result = service.getReceiver(idNumber);
        return result.map(receiverDto -> new ResponseEntity<>(receiverDto, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<ReceiverBasicDto>> getWithFilters(@RequestParam(defaultValue = "") String type,
                                                                 @RequestParam(defaultValue = "") String names,
                                                                 @RequestParam(defaultValue = "") String idNumber,
                                                                 @RequestParam @DateTimeFormat(pattern = "dd-MM-yy") LocalDate start,
                                                                 @RequestParam @DateTimeFormat(pattern = "dd-MM-yy") LocalDate end,
                                                                 @RequestParam(defaultValue = "names") String sort,
                                                                 @RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);
        if (start != null && end != null) {
            startDate = start;
            endDate = end;
        }
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(sort));
        var result = service.getWithFilters(type, names, idNumber, startDate, endDate, paging);
        if (result.getContent().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(result.getContent(), HttpStatus.OK);
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
    public ResponseEntity<Boolean> deleteReceiver(@PathVariable String idNumber) {
        boolean result = service.delete(idNumber);
        return result ? new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK) :
                new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
