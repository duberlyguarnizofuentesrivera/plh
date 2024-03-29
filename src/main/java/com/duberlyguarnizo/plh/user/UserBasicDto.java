package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserBasicDto extends RepresentationModel<UserBasicDto> {
    Long id;
    @NotBlank String firstName;
    @NotBlank String lastName;
    @NotBlank String idNumber;
    UserStatus status;
    UserRole role;
    @NotBlank @Length(min = 5) String username;
    String notes;
}