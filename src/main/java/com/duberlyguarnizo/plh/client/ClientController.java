package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    //CRUD methods
    @PostMapping("/create")
    public Client createClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }

    @PutMapping("/update")
    public Client updateClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteClient(@PathVariable("id") Long id) {
        clientRepository.deleteById(id);

    }

    @GetMapping("/{id}")
    public Client getClient(@PathVariable("id") Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    @GetMapping("/all")
    public Page<Client> getAllClient(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return clientRepository.findAll(PageRequest.of(page, size));

    }

    //Custom methods
    @GetMapping("/by-dni/{dni}")
    public Client getClientByDni(@PathVariable("dni") String dni) {
        List<Client> result = clientRepository.findByIdNumber(dni);
        if (result.isEmpty()) {
            return null;
        }
        //there should be only one result
        return result.get(0);
    }

    @GetMapping("/by-name/{name}")
    public List<Client> getClientByName(@PathVariable("name") String name) {
        List<Client> result = clientRepository.findByNames(name);
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }

    @GetMapping("/by-status/{status}")
    public List<Client> getClientByStatus(@PathVariable("status") String status) {
        try {
            UserStatus userStatus = UserStatus.valueOf(status);
            List<Client> result = clientRepository.findByStatus(userStatus);
            if (result.isEmpty()) {
                return Collections.emptyList();
            }
            return result;
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/by-date/{date}")
    public List<Client> getClientByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Client> result = clientRepository.findByModificationDateBetween(date.atStartOfDay(), date.atTime(23, 59, 59));
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }
}
