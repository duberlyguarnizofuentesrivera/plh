package com.duberlyguarnizo.plh.address;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/address")
public class AddressController {
    private final AddressRepository addressRepository;

    public AddressController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    //CRUD methods
    @PostMapping("/create")
    public Address createAddress(@RequestBody Address address) {
        return addressRepository.save(address);
    }

    @PutMapping("/update")
    public Address updateAddress(@RequestBody Address address) {
        return addressRepository.save(address);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAddress(@PathVariable("id") Long id) {
        addressRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public Address getAddress(@PathVariable("id") Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    @GetMapping("/all")
    public Page<Address> getAllAddress(@RequestParam(defaultValue = "10") Integer page, @RequestParam(defaultValue = "15") Integer size) {
        return addressRepository.findAll(PageRequest.of(page, size));
    }

    //Custom methods
    @GetMapping("/by-date/{date}")
    public List<Address> getAddressByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return addressRepository.
                findByLastModifiedDateBetween(date.atStartOfDay(),
                        date.atTime(23, 59, 59));
    }

    @GetMapping("/by-location/{region}")
    public List<Address> getAddressByRegion(@PathVariable("region") String region) {
        return addressRepository.findByRegion(region);
    }

    @GetMapping("/by-location/{region}/{province}")
    public List<Address> getAddressByProvince(
            @PathVariable("region") String region,
            @PathVariable("province") String province) {
        return addressRepository.findByRegionAndProvince(region, province);

    }

    @GetMapping("/by-location/{region}/{province}/{district}")
    public List<Address> getAddressByDistrict(
            @PathVariable("region") String region,
            @PathVariable("province") String province,
            @PathVariable("district") String district) {
        return addressRepository.
                findByRegionAndProvinceAndDistrict(
                        region,
                        province,
                        district);
    }
}
