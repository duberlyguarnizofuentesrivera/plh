package com.duberlyguarnizo.plh.receiver;

import jakarta.validation.Valid;
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
    public ResponseEntity<List<ReceiverBasicDto>> getWithFilters(@RequestParam(defaultValue = "all") String type,
                                                                 @RequestParam(defaultValue = "") String names,
                                                                 @RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(service.getWithFilters(type, names, page, size).getContent(), HttpStatus.OK);
    }

    @GetMapping("/date")
    public ResponseEntity<List<ReceiverBasicDto>> getByDate(@RequestParam @DateTimeFormat LocalDate date,
                                                            @RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(service.getByDate(date, page, size).getContent(), HttpStatus.OK);
    }

    @GetMapping("/date-interval")
    public ResponseEntity<List<ReceiverBasicDto>> getByDateInterval(@RequestParam @DateTimeFormat LocalDate date1,
                                                                    @RequestParam @DateTimeFormat LocalDate date2,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(service.getByDateInterval(date1, date2, page, size).getContent(), HttpStatus.OK);
    }

    @GetMapping("/date-days-ago")
    public ResponseEntity<List<ReceiverBasicDto>> getByDateDaysAgo(@RequestParam(defaultValue = "7") int days,
                                                                   @RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        var today = LocalDate.now();
        var daysAgo = LocalDate.now().minusDays(days);
        return new ResponseEntity<>(service.getByDateInterval(today, daysAgo, page, size).getContent(), HttpStatus.OK);
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
