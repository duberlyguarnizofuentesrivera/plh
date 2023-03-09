package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByIdNumber(String clientIdNumber);

    Page<Client> findByNamesContaining(String clientNames, Pageable pageable);

    Page<Client> findByStatus(UserStatus clientStatus, Pageable pageable);

    Page<Client> findByType(PersonType clientType, Pageable pageable);

    Page<Client> findByLastModifiedDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    Page<Client> findByTypeAndStatusAndNamesContaining(PersonType type, UserStatus status, String name, Pageable pageable);

    Page<Client> findByTypeAndStatus(PersonType type, UserStatus status, Pageable pageable);

    Page<Client> findByTypeAndNamesContains(PersonType type, String name, Pageable pageable);

    Page<Client> findByStatusAndNamesContains(UserStatus status, String name, Pageable pageable);

}
