package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * A DTO for the {@link Client} entity
 */
public record ClientBasicDto(String notes, @NotBlank String idNumber, PersonType type, @NotBlank String names,
                             String contactNames, UserStatus status) implements Serializable {
}