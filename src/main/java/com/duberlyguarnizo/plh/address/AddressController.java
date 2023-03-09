package com.duberlyguarnizo.plh.address;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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



    //Custom methods

}
