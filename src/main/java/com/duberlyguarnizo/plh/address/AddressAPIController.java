package com.duberlyguarnizo.plh.address;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/addresses")
public class AddressAPIController {
    AddressService service;

    public AddressAPIController(AddressService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long id) {
        var result = service.getDetailById(id);
        return result.map(addressDto ->
                        new ResponseEntity<>(addressDto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.OK));
    }

    @GetMapping("/date/{dateString}")
    public ResponseEntity<List<AddressBasicDto>> getAddressByDate(
            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dateString,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        var result = service.getAllByDate(dateString, page, size);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AddressBasicDto>> getAddressesWithFilters(@RequestParam(defaultValue = "all") String region,
                                                                         @RequestParam(defaultValue = "all") String province,
                                                                         @RequestParam(defaultValue = "all") String district,
                                                                         @RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        if (!"all".equals(district) && !"all".equals(province) && !"all".equals(region)) {
            //all 3 parameters were passed
            var result = service.getAllByRegionAndProvinceAndDistrict(region, province, district, page - 1, size);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if ("all".equals(district) && !"all".equals(province) && !"all".equals(region)) {
            //region and province were passed
            var result = service.getAllByRegionAndProvince(region, province, page - 1, size);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if ("all".equals(district) && "all".equals(province) && !"all".equals(region)) {
            var result = service.getAllByRegion(region, page - 1, size);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //---- CRUD----------------------
    @PostMapping
    public ResponseEntity<Map<String, String>> createAddress(@RequestBody AddressDto addressDto) {
        var result = service.save(addressDto);
        if (result) {
            return new ResponseEntity<>(Map.of("result", "CREATED"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(Map.of("result", "ERROR"), HttpStatus.CONFLICT);
        }
    }
}
