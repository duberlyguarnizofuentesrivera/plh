package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNames(String clientNames);

    List<Client> findByIdNumber(String clientIdNumber);

    List<Client> findByStatus(UserStatus clientStatus);

    List<Client> findByLastModifiedDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

}
