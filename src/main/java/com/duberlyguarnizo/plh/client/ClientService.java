package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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

    public Page<ClientBasicDto> getAllClients(int page, int size) {
        Page<Client> result = repository.findAll(PageRequest.of(page - 1, size));
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

    /**
     * Returns a Page of basic Client DTO that matches all or some of the parameters.
     *
     * @param type   The type of person, in string. Either "COMPANY", "PERSON", or  "all" must be passed.
     * @param status The status of the user, in string. Either "ACTIVE", "INACTIVE", or  "all" must be passed.
     * @param name   The query name, to be matched against the full name of the client.
     * @param page   The page of the request, starting from 1.
     * @param size   The number of elements per page.
     * @return Page of ClientBasicDto objects.
     */
    public Page<ClientBasicDto> getWithFilters(String type, String status, String name, int page, int size) {
        PersonType typeValue;
        UserStatus statusValue;
        try {
            typeValue = PersonType.valueOf(type);
        } catch (IllegalArgumentException e) {
            typeValue = null;
        }
        try {
            statusValue = UserStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            statusValue = null;
        }

        if (typeValue == null && statusValue == null && name.isEmpty()) {
            //all values are null, so we return complete list
            return getAllClients(page, size); //we do not rest 1 to the page bc the getAllClients function already does
        } else if (typeValue != null && statusValue != null && !name.isEmpty()) {
            //all values are set, so we search with all 3 parameters
            return repository.findByTypeAndStatusAndNamesContainingIgnoreCase(typeValue,
                    statusValue,
                    name,
                    PageRequest.of(page - 1, size)).map(mapper::toBasicDto);
        } else {
            //analyze case by case
            return getIterationResult(name, page, size, typeValue, statusValue);
        }
    }

    public Page<ClientBasicDto> getByDate(LocalDate date, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return repository.findByLastModifiedDateBetween(date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay(), pageRequest)
                .map(mapper::toBasicDto);
    }

    public Page<ClientBasicDto> getByDateInterval(LocalDate date1, LocalDate date2, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return repository.findByLastModifiedDateBetween(date1.atStartOfDay(),
                        date2.plusDays(1).atStartOfDay(), pageRequest)
                .map(mapper::toBasicDto);
    }


    //-------Utility methods-------------------------
    private Page<ClientBasicDto> getIterationResult(String name, int page, int size, PersonType typeValue, UserStatus statusValue) {
        PageRequest pageRequest = PageRequest.of(page - 1, size); //always rest 1 for repository index starts at 0
        if (name.isEmpty()) {
            if (typeValue == null && statusValue != null) {
                //search for status
                return repository.findByStatus(statusValue, pageRequest).map(mapper::toBasicDto);
            } else if (typeValue != null && statusValue == null) {
                //search for type
                return repository.findByType(typeValue, pageRequest).map(mapper::toBasicDto);
            } else {
                return repository.findByTypeAndStatus(typeValue, statusValue, pageRequest).map(mapper::toBasicDto);
            }
        } else {
            if (typeValue == null && statusValue != null) {
                //search for status
                return repository.findByStatusAndNamesContainsIgnoreCase(statusValue, name, pageRequest).map(mapper::toBasicDto);
            } else {
                //search for type
                return repository.findByTypeAndNamesContainsIgnoreCase(typeValue, name, pageRequest).map(mapper::toBasicDto);
            }
        }
    }
}