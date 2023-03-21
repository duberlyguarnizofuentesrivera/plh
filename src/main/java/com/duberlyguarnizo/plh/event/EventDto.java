package com.duberlyguarnizo.plh.event;

import com.duberlyguarnizo.plh.enums.EntityType;
import com.duberlyguarnizo.plh.enums.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link Event} entity
 */
public record EventDto(Long createdBy, LocalDateTime createdDate, Long lastModifiedBy, LocalDateTime lastModifiedDate,
                       String notes, Long id, Long entityId, EntityType entityType,
                       EventType eventType) implements Serializable {
}