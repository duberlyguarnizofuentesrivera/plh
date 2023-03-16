package com.duberlyguarnizo.plh.event;

import com.duberlyguarnizo.plh.enums.EntityType;
import com.duberlyguarnizo.plh.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    static Specification<Event> hasEntityId(Long entityId) {
        return (event, query, cb) -> cb.equal(event.get("entityId"), entityId);
    }

    static Specification<Event> isEntityType(EntityType entityType) {
        return (event, query, cb) -> cb.equal(event.get("entityType"), entityType);
    }

    static Specification<Event> isEventType(EventType eventType) {
        return (event, query, cb) -> cb.equal(event.get("eventType"), eventType);
    }

    static Specification<Event> wasGeneratedBy(Long userId) {
        return (event, query, cb) -> cb.equal(event.get("LastModifiedBy"), userId);
    }

    static Specification<Event> dateIsBetween(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();
        return (receiver, query, cb) -> cb.between(receiver.get("lastModifiedDate"), start, end);
    }

    Page<Event> findByEntityId(Long entityId, Pageable pageable);

    Page<Event> findByEntityType(EntityType entityType, Pageable pageable);

    Page<Event> findByEventType(EventType eventType, Pageable pageable);

    Page<Event> findByLastModifiedBy(Long userId, Pageable pageable);

}
