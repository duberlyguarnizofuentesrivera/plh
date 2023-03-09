package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link Client} entity
 */
public record ClientDetailDto(String notes, @NotBlank String idNumber, PersonType type, @NotBlank String names,
                              String contactNames, String phone, @Email String email, UserStatus status,
                              Set<Long> pickUpAddressIds) implements Serializable {
}