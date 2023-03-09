package com.duberlyguarnizo.plh.receiver;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

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
}
