package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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

    public Optional<ReceiverDetailDto> getDetailReceiver(String idNumber) {
        var receiverExists = repository.findByIdNumber(idNumber);
        if (receiverExists.isPresent()) {
            return Optional.of(mapper.toDetailDto(receiverExists.get()));
        } else {
            log.warn("ReceiverService: getReceiver(): Receiver with id number: {} doesn't exists", idNumber);
            return Optional.empty();
        }
    }

    public Page<ReceiverBasicDto> getWithFilters(String type, String names, int page, int size) {
        PersonType personType;
        try {
            personType = PersonType.valueOf(type);
        } catch (Exception e) {
            personType = null;
        }
        if (personType == null && names.isEmpty()) {
            //no filters applied
            return repository.findAll(PageRequest.of(page - 1, size)).map(mapper::toBasicDto);
        } else if (personType != null && !names.isEmpty()) {
            //both filters applied
            return repository.findByTypeAndNamesContainingIgnoreCase(personType, names, PageRequest.of(page - 1, size)).map(mapper::toBasicDto);
        } else {
            //one filter applied
            if (personType != null) {
                //filtering by type
                return repository.findByType(personType, PageRequest.of(page - 1, size)).map(mapper::toBasicDto);
            } else {
                //filtering by names
                return repository.findByNamesContainingIgnoreCase(names, PageRequest.of(page - 1, size)).map(mapper::toBasicDto);
            }
        }

    }

    public Page<ReceiverBasicDto> getByDate(LocalDate date, int page, int size) {
        return repository.findByLastModifiedDateBetween(date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay(),
                        PageRequest.of(page - 1, size))
                .map(mapper::toBasicDto);
    }

    public Page<ReceiverBasicDto> getByDateInterval(LocalDate date1, LocalDate date2, int page, int size) {
        return repository.findByLastModifiedDateBetween(date1.atStartOfDay(),
                        date2.plusDays(1).atStartOfDay(),
                        PageRequest.of(page - 1, size))
                .map(mapper::toBasicDto);
    }
}
