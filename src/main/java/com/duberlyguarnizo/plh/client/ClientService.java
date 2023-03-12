package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
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

    public boolean update(ClientDetailDto client, boolean create) {
        var userExists = repository.findByIdNumber(client.idNumber());
        if (userExists.isEmpty()) {
            if (create) {
                return save(client);
            } else {
                log.warn("ClientService: update(): client does not exists... can't update, and *create* argument is false, so no save action occurs.");
                return false;
            }
        } else {
            return save(client);
        }
    }

    public boolean save(ClientDetailDto client) {
        var userExists = repository.findByIdNumber(client.idNumber());
        if (userExists.isEmpty()) {
            try {
                repository.save(mapper.toEntity(client));
                return true;
            } catch (Exception e) {
                log.warn("ClientService: save(): Failed to save entity in repository. Message: {}", e.getMessage());
                return false;
            }
        } else {
            log.warn("ClientService: save(): Failed to save entity in repository, client already exist.");
            return false;
        }
    }

    public boolean delete(String idNumber) {
        Optional<Client> result = repository.findByIdNumber(idNumber);
        if (result.isPresent()) {
            try {
                repository.deleteById(result.get().getId());
                return true;
            } catch (Exception e) {
                log.warn("ClientService: delete(): Failed to delete entity in repository with id number {}, with message: {}", idNumber, e.getMessage());
                return false;
            }
        } else {
            log.warn("ClientService: delete(): Entity with id number {} does not exist, so cannot be deleted.", idNumber);
            return false;
        }

    }

    public Optional<ClientDetailDto> getClientById(Long id) {
        Optional<Client> result = repository.findById(id);
        return result.map(mapper::toDetailDto);
    }

    public Page<ClientBasicDto> getAllClients(PageRequest paging) {
        if (paging == null) {
            paging = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "names"));
        }
        Page<Client> result = repository.findAll(paging);
        return result.map(mapper::toBasicDto);
    }

    /**
     * Get list of clients whose name matches a query.
     *
     * @param name the name to search for. It may be some letters to be matched against the compete names of the client
     * @param page the number of the page requested (starting from 1)
     * @param size the number of elements per page
     * @return List of basic Dto for clients with a matching name
     */
    public Page<ClientBasicDto> getClientsByName(String name, int page, int size) {
        Page<Client> result = repository.findByNamesContainingIgnoreCase(name, PageRequest.of(page - 1, size));
        return result.map(mapper::toBasicDto);
    }

    public Optional<ClientBasicDto> getBasicClientByIdNumber(String idNumber) {
        var result = repository.findByIdNumber(idNumber);
        if (result.isEmpty()) {
            //There should be only one client with same idNumber
            log.warn("ClientService: getClientByIdNumber(): Can't find client with id: {}", idNumber);
            return Optional.empty();
        } else {
            return Optional.of(mapper.toBasicDto(result.get()));
        }
    }

    public Optional<ClientDto> getClientByIdNumber(String idNumber) {
        var result = repository.findByIdNumber(idNumber);
        if (result.isEmpty()) {
            //There should be only one client with same idNumber
            log.warn("ClientService: getClientByIdNumber(): Can't find client with id: {}", idNumber);
            return Optional.empty();
        } else {
            return Optional.of(mapper.toDto(result.get()));
        }
    }

    public Page<ClientBasicDto> getWithFilters(String name,
                                               String type,
                                               String status,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               PageRequest paging) {
        PersonType typeValue;
        UserStatus statusValue;
        if (paging == null) {
            paging = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "names"));
        }
        try {
            typeValue = PersonType.valueOf(type);

        } catch (Exception e) {
            typeValue = null;
        }
        try {
            statusValue = UserStatus.valueOf(status);

        } catch (Exception e) {
            statusValue = null;
        }
        try {
            return repository.findAll(where(isType(typeValue))
                    .and(hasUserStatus(statusValue))
                    .and(nameContains(name))
                    .and(dateIsBetween(startDate, endDate)), paging).map(mapper::toBasicDto);
        } catch (Exception e) {
            log.warn("ClientService: getWithFilters2(): Failed to get clients with filters. Message: {}", e.getMessage());
            return Page.empty();
        }
    }

    //-------Utility methods-------------------------

}