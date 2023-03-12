package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    Page<Receiver> findByNames(String receiverNames, Pageable pageable);

    Optional<Receiver> findByIdNumber(String receiverIdNumber);

    Page<Receiver> findByType(PersonType type, Pageable pageable);

    Page<Receiver> findByTypeAndNamesContainingIgnoreCase(PersonType type, String names, Pageable pageable);

    Page<Receiver> findByNamesContainingIgnoreCase(String names, Pageable pageable);

    Page<Receiver> findByLastModifiedDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

}
