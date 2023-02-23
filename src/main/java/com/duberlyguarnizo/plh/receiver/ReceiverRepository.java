package com.duberlyguarnizo.plh.receiver;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Long> {
    Page<Receiver> findByNames(String receiverNames, Pageable pageable);

    List<Receiver> findByIdNumber(String receiverIdNumber);

    List<Receiver> findByCompany(boolean isCompany);

    List<Receiver> findByLastModifiedDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

}
