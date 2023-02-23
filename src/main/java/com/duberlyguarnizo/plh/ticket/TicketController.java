package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {
    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    //CRUD methods
    @PostMapping("/create")
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @PutMapping("/update")
    public Ticket updateTicket(@RequestBody Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTicket(@PathVariable("id") Long id) {
        ticketRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public Ticket getTicket(@PathVariable("id") Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @GetMapping("/all")
    public Page<Ticket> getAllTicket(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return ticketRepository.findAll(PageRequest.of(page, size));
    }

    //Custom methods
    @GetMapping("/by-client/{id}")
    public List<Ticket> getTicketByClientId(@PathVariable("id") Long id) {
        return ticketRepository.findByClient_Id(id);
    }

    @GetMapping("/by-client-dni/{dni}")
    public List<Ticket> getTicketByClientId(@PathVariable("dni") String dni) {
        return ticketRepository.findByClient_IdNumber(dni);
    }

    @GetMapping("/by-code/{code}")
    public Ticket getTicketByCode(@PathVariable("code") String code) {
        List<Ticket> result = ticketRepository.findByCode(code);
        //there should be only one ticket with the same code
        return result.get(0);
    }

    @GetMapping("/by-date/{date}")
    public List<Ticket> getTicketsByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ticketRepository.findByLastModifiedDateBetween(date.atStartOfDay(), date.atTime(23, 59, 59));
    }

    @GetMapping("/by-payment/{payment}")
    public List<Ticket> getTicketsByPaymentStatus(@PathVariable("payment") String paymentStatus, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        try {
            TicketPaymentStatus ticketPaymentStatus = TicketPaymentStatus.valueOf(paymentStatus);
            return ticketRepository.findByPaymentStatus(ticketPaymentStatus, PageRequest.of(page, size));
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/by-status/{status}")
    public List<Ticket> getTicketsByStatus(@PathVariable("status") String status, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        try {
            TicketStatus ticketStatus = TicketStatus.valueOf(status);
            return ticketRepository.findByStatus(ticketStatus, PageRequest.of(page, size));
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/by-user-id/{id}")
    public List<Ticket> getTicketsByUserId(@PathVariable("id") Long id, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return ticketRepository.findByUser_Id(id, PageRequest.of(page, size));
    }
}
