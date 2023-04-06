package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.address.Address;
import com.duberlyguarnizo.plh.address.AddressBasicDto;
import com.duberlyguarnizo.plh.address.AddressMapper;
import com.duberlyguarnizo.plh.address.AddressRepository;
import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import com.duberlyguarnizo.plh.util.PlhException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.duberlyguarnizo.plh.client.ClientRepository.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
public class ClientService {
    private final AddressRepository addressRepository;
    private final ClientRepository repository;
    private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);
    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    public ClientService(ClientRepository repository,
                         AddressRepository addressRepository) {
        this.repository = repository;
        this.addressRepository = addressRepository;
    }

    public boolean update(ClientDetailDto client) throws PlhException {
        var userExists = repository.findById(client.getId());
        if (userExists.isEmpty()) {
            log.warn("ClientService: update(): client does not exists... can't update.");
            return false;
        } else {
            try {
                repository.save(mapper.partialUpdate(client, userExists.get()));
                return true;
            } catch (Exception e) {
                throw new PlhException(e, "ClientService: update(): Error updating entity in repository. Message: " + e.getMessage());
            }
        }
    }

    /**
     * Saves a new client to the database or updates an existing one. If the passed client has a non-null ID and
     * already exists in the database, the method returns false and does nothing. If the ID is null or the client does not
     * exist, the client is inserted into the database along with any pickup addresses associated with it.
     *
     * @param client a ClientDetailDto representing the client to save
     * @return true if the client was successfully saved, false if the client already exists in the database
     * @throws PlhException             if there was a problem saving the client or associated pickup addresses
     * @throws NullPointerException     if the passed client is null
     * @throws IllegalArgumentException if the passed client has invalid or incomplete data that prevents it from being saved
     */
    public boolean save(ClientDetailDto client) throws PlhException {
        boolean idIsNull = client.getId() == null;
        boolean clientExists = false;
        if (!idIsNull) {
            clientExists = repository.existsById(client.getId());
        }
        if (!clientExists) {
            try {
                var clientToSave = mapper.toEntity(client);
                var savedClient = repository.save(clientToSave);
                var passedAddresses = client.getPickUpAddress();
                Set<Address> newAddresses = new HashSet<>();
                //if there are addresses, save them in DB, and then link them to client entity, and re-save.
                if (!passedAddresses.isEmpty()) {
                    for (Address address : passedAddresses) {
                        newAddresses.add(addressRepository.save(address));
                    }
                    savedClient.setPickUpAddresses(newAddresses);
                    repository.save(savedClient);
                }
                return true;
            } catch (Exception e) {
                throw new PlhException(e, "ClientService: save(): Error saving entity in repository. Message: " + e.getMessage());
            }
        } else {
            log.warn("ClientService: save(): Failed to save entity in repository, client already exist.");
            return false;
        }
    }

    /**
     * Save a single address into the database, and then add it to the client's addresses list.
     *
     * @param clientId   the client id
     * @param addressDto the address dto
     * @return <b>true</b> if the address is created and saved successfully, <b>false</b> otherwise
     * @throws PlhException if any error occurs during saving or reading of the address or client
     */
    public boolean saveSingleAddress(Long clientId, AddressBasicDto addressDto) throws PlhException {
        try {
            var client = repository.findById(clientId);
            var addressEntity = addressMapper.toEntity(addressDto);
            //No checking if address exists, bc one single address can be used by multiple different clients in the future
            if (client.isPresent()) {
                var clientEntity = client.get();
                var currentAddresses = clientEntity.getPickUpAddresses();
                var addressResult = addressRepository.save(addressEntity);
                currentAddresses.add(addressResult);
                clientEntity.setPickUpAddresses(currentAddresses);
                repository.save(clientEntity);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new PlhException(e, "Failed to add new address to client with ID: " + clientId + " and message: " + e.getMessage());
        }
    }

    /**
     * Delete a single address from a client addresses list, and from the database.
     *
     * @param clientId  the client id
     * @param addressId the address id
     * @return <b>true</b>, if the deletion was successful, or <b>false</b> otherwise
     * @throws PlhException if any error occurs during saving, reading ot getting address or client information
     */
    public boolean deleteSingleAddress(Long clientId, Long addressId) throws PlhException {
        try {
            var client = repository.findById(clientId);
            var address = addressRepository.findById(addressId);
            if (client.isPresent() && address.isPresent()) {
                var clientEntity = client.get();
                var currentAddressesSet = clientEntity.getPickUpAddresses();
                var newAddressesSet = currentAddressesSet.stream()
                        .filter(add -> !add.getId().equals(addressId)) //filter in only address does not exist in set
                        .collect(Collectors.toSet());
                clientEntity.setPickUpAddresses(newAddressesSet);
                repository.save(clientEntity);
                addressRepository.deleteById(addressId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new PlhException(e, "Failed to add new address to client with ID: " + clientId + " and message: " + e.getMessage());
        }
    }

    public boolean delete(Long id) throws PlhException {
        Optional<Client> result = repository.findById(id);
        if (result.isPresent()) {
            try {
                repository.deleteById(result.get().getId());
                return true;
            } catch (Exception e) {
                throw new PlhException(e, "ClientService: delete(): Error deleting entity in repository. Message: " + e.getMessage());
            }
        } else {
            log.warn("ClientService: delete(): Entity with id  {} does not exist, so cannot be deleted.", id);
            return false;
        }
    }

    public Optional<ClientDto> getClientById(Long id) throws PlhException {
        try {
            Optional<Client> result = repository.findById(id);
            return result.map(mapper::toDto);
        } catch (Exception e) {
            throw new PlhException(e, "ClientService: getClientById(): " +
                    "error with argument or error mapping optional. Message: " + e.getMessage());
        }
    }

    public Page<ClientBasicDto> getAll(String name,
                                       String clientType,
                                       String status,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       PageRequest paging) throws PlhException {
        PersonType typeValue;
        UserStatus statusValue;
        if (paging == null) {
            paging = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "names"));
        }
        try {
            typeValue = PersonType.valueOf(clientType);
        } catch (IllegalArgumentException e) {
            if (clientType.equals("all")) {
                typeValue = null;
            } else {
                throw new PlhException(e, "ClientService: getAll(): 'clientType' argument is not a valid PersonType, nor 'all'. Message: " + e.getMessage());
            }
        }
        try {
            statusValue = UserStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            if (status.equals("all")) {
                statusValue = null;
            } else {
                throw new PlhException(e, "ClientService: getAll(): 'status' argument is not a valid UserStatus, nor 'all'. Message: " + e.getMessage());
            }
        }
        try {
            var result = repository.findAll(where(isType(typeValue))
                    .and(hasUserStatus(statusValue))
                    .and(nameContains(name))
                    .and(dateIsBetween(startDate, endDate)), paging);
            if (result == null) { //for some reason, with invalid parameters this returns null (when mocking in tests)! (not possible to debug)
                return Page.empty();
            }
            return result.map(mapper::toBasicDto);
        } catch (Exception e) {
            throw new PlhException(e, "ClientService: getAll(): Failed to get list of clients with filters. Message: " + e.getMessage());
        }
    }

    public boolean toggleStatus(Long id) {
        try {
            var clientExists = repository.findById(id);
            if (clientExists.isPresent()) {
                var client = clientExists.get();
                var currentStatus = client.getStatus();
                if (currentStatus == UserStatus.ACTIVE) {
                    currentStatus = UserStatus.INACTIVE;
                } else {
                    currentStatus = UserStatus.ACTIVE;
                }
                client.setStatus(currentStatus);
                repository.save(client);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new PlhException(e, "ClientService: toggleStatus(): Failed to change status of client with id: " + id);
        }
    }
}