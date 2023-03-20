package com.duberlyguarnizo.plh.client;

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
import java.util.Optional;

import static com.duberlyguarnizo.plh.client.ClientRepository.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
public class ClientService {
    private final ClientRepository repository;
    private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    public ClientService(ClientRepository repository) {
        this.repository = repository;
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
                throw new PlhException("ClientService: update(): Error updating entity in repository. Message: " + e.getMessage());
            }
        }
    }

    public boolean save(ClientDetailDto client) throws PlhException {
        var userExists = repository.findByIdNumber(client.getIdNumber());
        if (userExists.isEmpty()) {
            try {
                repository.save(mapper.toEntity(client));
                return true;
            } catch (Exception e) {
                throw new PlhException("ClientService: save(): Error saving entity in repository. Message: " + e.getMessage());
            }
        } else {
            log.warn("ClientService: save(): Failed to save entity in repository, client already exist.");
            return false;
        }
    }

    public boolean delete(Long id) throws PlhException {
        Optional<Client> result = repository.findById(id);
        if (result.isPresent()) {
            try {
                repository.deleteById(result.get().getId());
                return true;
            } catch (Exception e) {
                throw new PlhException("ClientService: delete(): Error deleting entity in repository. Message: " + e.getMessage());
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
            throw new PlhException("ClientService: getClientById(): " +
                    "error with argument or error mapping optional. Message: " + e.getMessage());
        }
    }

    public Page<ClientBasicDto> getAll(String name,
                                       String type,
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
            typeValue = PersonType.valueOf(type);

        } catch (IllegalArgumentException e) {
            if (type.equals("all")) {
                typeValue = null;
            } else {
                throw new PlhException("ClientService: getAll(): 'type' argument is not a valid PersonType, nor 'all'. Message: " + e.getMessage());
            }
        }
        try {
            statusValue = UserStatus.valueOf(status);

        } catch (IllegalArgumentException e) {
            if (status.equals("all")) {
                statusValue = null;
            } else {
                throw new PlhException("ClientService: getAll(): 'status' argument is not a valid UserStatus, nor 'all'. Message: " + e.getMessage());
            }
        }
        try {
            return repository.findAll(where(isType(typeValue))
                    .and(hasUserStatus(statusValue))
                    .and(nameContains(name))
                    .and(dateIsBetween(startDate, endDate)), paging).map(mapper::toBasicDto);
        } catch (Exception e) {
            throw new PlhException("ClientService: getAll(): Failed to get list of clients with filters. Message: " + e.getMessage());
        }
    }

    //-------Utility methods-------------------------

}