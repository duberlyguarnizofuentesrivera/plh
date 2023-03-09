package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * A DTO for the {@link Receiver} entity
 */
public record ReceiverBasicDto(String notes, PersonType type, @NotBlank String names, String idNumber,
                               String contactNames) implements Serializable {
}