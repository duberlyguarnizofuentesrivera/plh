package com.duberlyguarnizo.plh.shipment;

import com.duberlyguarnizo.plh.enums.ShipmentProblem;
import com.duberlyguarnizo.plh.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/shipments")
public class ShipmentController {
    private final ShipmentRepository shipmentRepository;

    public ShipmentController(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    //CRUD methods
    @PostMapping("/create")
    public Shipment createShipment(@RequestBody Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    @PutMapping("/update")
    public Shipment updateShipment(@RequestBody Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteShipment(@PathVariable("id") Long id) {
        shipmentRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public Shipment getShipment(@PathVariable("id") Long id) {
        return shipmentRepository.findById(id).orElse(null);
    }

    @GetMapping("/all")
    public Page<Shipment> getAllShipment(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findAll(PageRequest.of(page, size));
    }

    //Custom methods
    @GetMapping("/by-client-id/{id}")
    public List<Shipment> getShipmentsByClientId(@PathVariable("id") Long id) {
        return shipmentRepository.findByClient_Id(id);
    }

    @GetMapping("/by-transporter-id/{id}")
    public List<Shipment> getShipmentsByTransporterId(@PathVariable("id") Long id, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByDeliveryTransporter_Id(id, PageRequest.of(page, size));
    }

    @GetMapping("/by-pick-up-transporter-id/{id}")
    public List<Shipment> getShipmentsByPickUpTransporterId(@PathVariable("id") Long id, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByPickUpTransporter_Id(id, PageRequest.of(page, size));
    }

    @GetMapping("/by-receiving-user-id/{id}")
    public List<Shipment> getShipmentsByReceivingUserId(@PathVariable("id") Long id, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByReceiverUser_Id(id, PageRequest.of(page, size));
    }

    @GetMapping("/by-receiver-id/{id}")
    public List<Shipment> getShipmentsByReceiverId(@PathVariable("id") Long id) {
        return shipmentRepository.findByReceiver_Id(id);
    }

    @GetMapping("/by-ticket-code/{code}")
    public List<Shipment> getShipmentsByTicketCode(@PathVariable("code") String code) {
        return shipmentRepository.findByCode(code);
    }

    @GetMapping("/by-date/{date}")
    public List<Shipment> getShipmentByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByLastModifiedDateBetween(date.atStartOfDay(), date.atTime(13, 59, 59), PageRequest.of(page, size));
    }

    @GetMapping("/by-delivered")
    public List<Shipment> getShipmentsDeliveredDateSet(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByDeliveryDateNotNull(PageRequest.of(page, size));
    }

    @GetMapping("/by-on-return")
    public List<Shipment> getShipmentsOnReturnDateSet(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByOnReturnDateNotNull(PageRequest.of(page, size));
    }

    @GetMapping("/by-on-route")
    public List<Shipment> getShipmentsOnRouteDateSet(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByOnRouteDateNotNull(PageRequest.of(page, size));
    }

    @GetMapping("/by-returned")
    public List<Shipment> getShipmentsReturnDateSet(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByReturnDateNotNull(PageRequest.of(page, size));
    }

    @GetMapping("/by-received")
    public List<Shipment> getShipmentsReceptionDateSet(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return shipmentRepository.findByReceptionDateNotNull(PageRequest.of(page, size));
    }

    @GetMapping("/by-status/{status}")
    public List<Shipment> getShipmentsByStatus(@PathVariable("status") String status, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        try {
            ShipmentStatus shipmentStatus = ShipmentStatus.valueOf(status);
            return shipmentRepository.findByStatus(shipmentStatus, PageRequest.of(page, size));
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/by-problems/{problem}")
    public List<Shipment> getShipmentsByProblem(@PathVariable("problem") String problem, @RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        try {
            ShipmentProblem shipmentProblem = ShipmentProblem.valueOf(problem);
            return shipmentRepository.findByProblems(shipmentProblem, PageRequest.of(page, size));
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }
}
