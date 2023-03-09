package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/receiver")
public class ReceiverController {
    private final ReceiverRepository receiverRepository;

    public ReceiverController(ReceiverRepository receiverRepository) {
        this.receiverRepository = receiverRepository;
    }

    //CRUD methods
    @PostMapping("/create")
    public Receiver createReceiver(@RequestBody Receiver receiver) {
        return receiverRepository.save(receiver);
    }

    @PutMapping("/update")
    public Receiver updateReceiver(@RequestBody Receiver receiver) {
        return receiverRepository.save(receiver);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteReceiver(@PathVariable("id") Long id) {
        receiverRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public Receiver getReceiver(@PathVariable("id") Long id) {
        return receiverRepository.findById(id).orElse(null);
    }

    @GetMapping("/all")
    public Page<Receiver> getAllReceivers(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return receiverRepository.findAll(PageRequest.of(page, size));
    }

    //Custom methods
    @GetMapping("/by-name/{name}")
    public Page<Receiver> getReceiversByName(@PathVariable("name") String name, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return receiverRepository.findByNames(name, PageRequest.of(page, size));
    }

    @GetMapping("/by-dni/{dni}")
    public Receiver getReceiverByDni(@PathVariable("dni") String dni) {
        List<Receiver> result = receiverRepository.findByIdNumber(dni);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    @GetMapping("/by-is-company")
    public List<Receiver> getReceiversThatAreCompanies() {
        List<Receiver> result = receiverRepository.findByType(PersonType.COMPANY);
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }

    @GetMapping("/by-is-not-company")
    public List<Receiver> getReceiversThatAreNotCompanies() {
        List<Receiver> result = receiverRepository.findByType(PersonType.PERSON);
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }

    @GetMapping("/by-date/{date}")
    public List<Receiver> getReceiversByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return receiverRepository.findByLastModifiedDateBetween(date.atStartOfDay(), date.atTime(23, 59, 59));
    }
}
