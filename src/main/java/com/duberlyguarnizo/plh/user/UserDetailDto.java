package com.duberlyguarnizo.plh.user;

import com.duberlyguarnizo.plh.enums.UserRole;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class UserDetailDto extends RepresentationModel<UserDetailDto> {
    Long id;
    @NotBlank String firstName;
    @NotBlank String lastName;
    @NotBlank String idNumber;
    String phone;
    String phone2;
    @Email String email;
    UserStatus status;
    UserRole role;
    @NotBlank @Length(min = 5) String username;
    @NotBlank String password;
}