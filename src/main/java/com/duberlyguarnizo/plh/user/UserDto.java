package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link User} entity
 */
public record UserDto(Long createdBy, LocalDateTime createdDate, Long lastModifiedBy, LocalDateTime lastModifiedDate,
                      @NotBlank String firstName, @NotBlank String lastName, @NotBlank String idNumber, String phone,
                      String phone2, @Email String email, UserStatus status, UserRole role,
                      @NotBlank @Length(min = 5) String username, @NotBlank String password,
                      String notes) implements Serializable {
}