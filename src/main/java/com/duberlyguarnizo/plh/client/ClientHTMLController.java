package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.address.AddressService;
import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RequestMapping("/system/clients")
@Controller
public class ClientHTMLController {
    private final ClientService clientService;
    private final UserService userService;
    private final AddressService addressService;

    public ClientHTMLController(ClientService clientService, UserService userService, AddressService addressService) {
        this.clientService = clientService;
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public String listAllClients(Model model) {
        model.addAttribute("typesList", PersonType.values());
        model.addAttribute("statusList", UserStatus.values());
        return "system/clients/list";
    }

    @GetMapping("/create")
    public String createClient(Model model) {
        model.addAttribute("typesList", PersonType.values());
        model.addAttribute("statusList", UserStatus.values());
        return "/system/clients/create";
    }

    @GetMapping("{id}")
    public String clientDetail(@PathVariable Long id, Model model) {
        var possibleClient = clientService.getClientById(id);
        if (possibleClient.isPresent()) {
            var client = possibleClient.get();
            var createdByName = userService.getById(client.getCreatedBy());
            var modifiedByName = userService.getById(client.getCreatedBy());
            var addresses = client.getPickUpAddressIds()
                    .stream()
                    .map(addressService::getDetailById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            model.addAttribute("client", client);
            if (createdByName.isPresent()) {
                model.addAttribute("createdByName", createdByName.get().getUsername());
            } else {
                model.addAttribute("createdByName", "Sin Datos");
            }
            if (modifiedByName.isPresent()) {
                model.addAttribute("modifiedByName", modifiedByName.get().getUsername());
            } else {
                model.addAttribute("modifiedByName", "Sin Datos");
            }
            model.addAttribute("clientTypes", PersonType.values());
            model.addAttribute("addresses", addresses);
        } else {
            return "error";
            //TODO: make error page show some feedback on origin of the error
        }
        return "/system/clients/detail";
    }
}
