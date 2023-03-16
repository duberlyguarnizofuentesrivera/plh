package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data

public class UserDto extends RepresentationModel<UserDto> {
    Long id;
    Long createdBy;
    LocalDateTime createdDate;
    Long lastModifiedBy;
    LocalDateTime lastModifiedDate;
    @NotBlank String firstName;
    @NotBlank String lastName;
    @NotBlank String idNumber;
    String phone;
    String phone2;
    @Email String email;
    UserStatus status;
    UserRole role;
    @NotBlank @Length(min = 5) String username;
    String notes;
}