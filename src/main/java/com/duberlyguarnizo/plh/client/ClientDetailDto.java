package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.address.Address;
import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link Client} entity
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class ClientDetailDto extends RepresentationModel<ClientDetailDto> implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final Long id;
    private final String notes;//
    private final @NotBlank String idNumber;//
    private final PersonType clientType;//
    private final @NotBlank String names;//
    private final String contactNames;//
    private final String phone;//
    private final @Email String email;//
    private final UserStatus status;
    private final Set<Address> pickUpAddress;
}