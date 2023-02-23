package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * A DTO for the {@link User} entity
 */
public record UserBasicDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        UserStatus status,
        @NotBlank @Length(min = 5) String username)
        implements Serializable {
}