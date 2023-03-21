package com.duberlyguarnizo.plh.event;

import com.duberlyguarnizo.plh.enums.EntityType;
import com.duberlyguarnizo.plh.enums.EventType;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.duberlyguarnizo.plh.event.EventRepository.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Slf4j
public class EventService {
    private final EventRepository repository;
    private final EventMapper mapper = Mappers.getMapper(EventMapper.class);

    @Autowired
    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Page<EventDto> getWithFilters(Long modifiedBy, Long entityId, EntityType entityType, EventType eventType, LocalDate startDate, LocalDate endDate, PageRequest paging) {
        return repository.findAll(where(wasGeneratedBy(modifiedBy))
                        .and(hasEntityId(entityId))
                        .and(isEntityType(entityType))
                        .and(isEventType(eventType))
                        .and(dateIsBetween(startDate, endDate)), paging)
                .map(mapper::toDto);
    }
}
