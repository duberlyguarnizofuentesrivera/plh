package com.duberlyguarnizo.plh.address;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * A DTO for the {@link Address} entity
 */
public record AddressBasicDto(Long id, String notes, String region, String province, String district,
                              @NotBlank String addressLine, String observations) implements Serializable {
}