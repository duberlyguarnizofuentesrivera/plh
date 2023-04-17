package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.util.PlhException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.duberlyguarnizo.plh.receiver.ReceiverRepository.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Slf4j
public class ReceiverService {
    private final ReceiverRepository repository;
    private final ReceiverMapper mapper = Mappers.getMapper(ReceiverMapper.class);

    public ReceiverService(ReceiverRepository repository) {
        this.repository = repository;
    }

    public boolean save(ReceiverDetailDto dto) {
        var receiverExists = repository.findByIdNumber(dto.idNumber());
        if (receiverExists.isEmpty()) {
            try {
                repository.save(mapper.toEntity(dto));
                return true;
            } catch (Exception e) {
                log.error("ReceiverService: save(): Error saving receiver with id number: {}", dto.idNumber());
                return false;
            }
        } else {
            log.warn("ReceiverService: save(): Receiver with id number: {} already exists", dto.idNumber());
            return false;
        }
    }

    public boolean update(ReceiverDetailDto dto, boolean create) {
        var receiverExists = repository.findByIdNumber(dto.idNumber());
        if (receiverExists.isPresent()) {
            try {
                repository.save(mapper.toEntity(dto));
                return true;
            } catch (Exception e) {
                log.error("ReceiverService: update(): Error updating receiver with id number: {}", dto.idNumber());
                return false;
            }
        } else {
            if (create) {
                save(dto);
                log.info("ReceiverService: update(): Receiver with id number: {} doesn't exists, but *create* flag is true, so a new receiver will be created", dto.idNumber());
            } else {
                log.info("ReceiverService: update(): Receiver with id number: {} does not exist and *create* flag is false, therefore nothing happens", dto.idNumber());
            }
            return false;
        }
    }

    public boolean delete(String idNumber) {
        var receiverExists = repository.findByIdNumber(idNumber);
        if (receiverExists.isPresent()) {
            try {
                repository.deleteById(receiverExists.get().getId());
                return true;
            } catch (Exception e) {
                log.error("ReceiverService: delete(): Error deleting receiver with id number: {}", idNumber);
                return false;
            }
        } else {
            log.warn("ReceiverService: delete(): Receiver with id number: {} doesn't exists", idNumber);
            return false;
        }
    }

    public Optional<ReceiverDto> getReceiver(String idNumber) {
        var receiverExists = repository.findByIdNumber(idNumber);
        if (receiverExists.isPresent()) {
            return Optional.of(mapper.toDto(receiverExists.get()));
        } else {
            log.warn("ReceiverService: getReceiver(): Receiver with id number: {} doesn't exists", idNumber);
            return Optional.empty();
        }
    }

    public Optional<ReceiverDto> getReceiverById(Long id) throws PlhException {
        try {
            Optional<Receiver> result = repository.findById(id);
            return result.map(mapper::toDto);
        } catch (Exception e) {
            throw new PlhException(e, "ReceiverService: getReceiverById(): " +
                    "error with argument or error mapping optional. Message: " + e.getMessage());
        }
    }

    public Optional<ReceiverDetailDto> getDetailReceiver(String idNumber) {
        var receiverExists = repository.findByIdNumber(idNumber);
        if (receiverExists.isPresent()) {
            return Optional.of(mapper.toDetailDto(receiverExists.get()));
        } else {
            log.warn("ReceiverService: getReceiver(): Receiver with id number: {} doesn't exists", idNumber);
            return Optional.empty();
        }
    }

    /**
     * Gets a Page of ReceiverBasicDto filtered by some fields
     *
     * @param type     the type of person. Allowed values: "PERSON", "COMPANY". Other value suppresses this filter.
     * @param names    the names or part of the name of the receiver. If empty, the filter is not applied.
     * @param idNumber the id card number of the receiver. If empty, the filter is not applied.
     * @param start    the start date of last modification/creation of the receiver. If null, default is 7 days ago
     * @param end      the end date of last modification/creation of the receiver. If null, default is today.
     * @param paging   the paging, composed of page number, elements per page and sort field. If null,
     *                 defaults to first page of 10 elements and sort by receiver's names.
     * @return the page with filters applied, or an empty page if there was an error querying the repository.
     */
    public Page<ReceiverBasicDto> getAll(String type,
                                         String names,
                                         String idNumber,
                                         LocalDate start,
                                         LocalDate end,
                                         PageRequest paging) throws PlhException {
        PersonType typeValue;
        try {
            typeValue = PersonType.valueOf(type);
        } catch (Exception e) {
            typeValue = null;
        }
        if (paging == null) {
            paging = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "names"));
        }
        try {
            return repository.findAll(where(hasType(typeValue))
                            .and(nameContains(names))
                            .and(idNumberIs(idNumber))
                            .and(dateIsBetween(start, end)), paging)
                    .map(mapper::toBasicDto);
        } catch (Exception e) {
            throw new PlhException(e, "ReceiverService: getWithFilters(): Error trying to query from repository. Error is: " + e.getMessage());
        }
    }

}
