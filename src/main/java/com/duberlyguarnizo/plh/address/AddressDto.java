package com.duberlyguarnizo.plh.address;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link Address} entity
 */
public record AddressDto(Long id, String notes, Long createdBy, LocalDateTime createdDate, Long lastModifiedBy,
                         LocalDateTime lastModifiedDate, String region, String province, String district,
                         @NotBlank String addressLine, String observations) implements Serializable {
}