package com.duberlyguarnizo.plh.receiver;

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

@RequestMapping("/system/receivers")
@Controller
public class ReceiverHTMLController {
    private final ReceiverService receiverService;
    private final UserService userService;
    private final AddressService addressService;

    public ReceiverHTMLController(ReceiverService receiverService, UserService userService, AddressService addressService) {
        this.receiverService = receiverService;
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public String listAllReceivers(Model model) {
        model.addAttribute("typesList", PersonType.values());
        model.addAttribute("statusList", UserStatus.values());
        return "system/receivers/list";
    }

    @GetMapping("/create")
    public String createReceiver(Model model) {
        model.addAttribute("typesList", PersonType.values());
        return "/system/receivers/create";
    }

    @GetMapping("{id}")
    public String receiverDetail(@PathVariable Long id, Model model) {
        var possibleReceiver = receiverService.getReceiverById(id);
        if (possibleReceiver.isPresent()) {
            var receiver = possibleReceiver.get();
            var createdByName = userService.getById(receiver.getCreatedBy());
            var modifiedByName = userService.getById(receiver.getCreatedBy());
            var addresses = receiver.getAddressIds()
                    .stream()
                    .map(addressService::getDetailById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            model.addAttribute("receiver", receiver);
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
            model.addAttribute("receiverTypes", PersonType.values());
            model.addAttribute("addresses", addresses);
        } else {
            return "error";
            //TODO: make error page show some feedback on origin of the error
        }
        return "/system/receivers/detail";
    }
}
