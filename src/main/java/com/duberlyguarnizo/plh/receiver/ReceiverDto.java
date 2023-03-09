package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * A DTO for the {@link Receiver} entity
 */
public record ReceiverDto(Long createdBy, LocalDateTime createdDate, Long lastModifiedBy,
                          LocalDateTime lastModifiedDate, String notes, Long id, PersonType type,
                          @NotBlank String names, String idNumber, String phone, @Email String email,
                          String contactNames, Set<Long> addressIds) implements Serializable {
}