package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    static Specification<Client> isType(PersonType type) {
        if (type != null) {
            return (client, query, cb) -> cb.equal(client.get("type"), type);
        } else {
            return null;
        }
    }

    static Specification<Client> hasUserStatus(UserStatus status) {
        if (status != null) {
            return (client, query, cb) -> cb.equal(client.get("status"), status);
        } else {
            return null;
        }
    }

    static Specification<Client> nameContains(String name) {
        if (name.isEmpty()) {
            return null;
        } else {
            return (client, query, cb) -> cb.like(client.get("names"), "%" + name + "%");
        }
    }

    static Specification<Client> dateIsBetween(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();
        return (client, query, cb) -> cb.between(client.get("lastModifiedDate"), start, end);
    }

    Optional<Client> findByIdNumber(String clientIdNumber);

    Page<Client> findByNamesContainingIgnoreCase(String clientNames, Pageable pageable);

}
