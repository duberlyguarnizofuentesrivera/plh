package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Long>, JpaSpecificationExecutor<Receiver> {

    static Specification<Receiver> hasType(PersonType type) {
        if (type != null) {
            return (receiver, query, cb) -> cb.equal(receiver.get("type"), type);
        } else {
            return null;
        }
    }

    static Specification<Receiver> nameContains(String name) {
        if (name.isEmpty()) {
            return null;
        } else {
            return (receiver, query, cb) -> cb.like(receiver.get("names"), "%" + name + "%");
        }
    }

    static Specification<Receiver> idNumberIs(String idNumber) {
        if (idNumber.isEmpty()) {
            return null;
        } else {
            return (receiver, query, cb) -> cb.equal(receiver.get("idNumber"), idNumber);
        }
    }

    static Specification<Receiver> dateIsBetween(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();
        return (receiver, query, cb) -> cb.between(receiver.get("lastModifiedDate"), start, end);
    }


    Optional<Receiver> findByIdNumber(String receiverIdNumber);

}
