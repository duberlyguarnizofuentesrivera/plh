package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;

/**
 * A DTO for the {@link Client} entity
 */
@Data
@AllArgsConstructor
@Builder
public final class ClientBasicDto extends RepresentationModel<ClientBasicDto> implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final Long id;
    private final String notes;
    private final @NotBlank String idNumber;
    private final PersonType type;
    private final @NotBlank String names;
    private final String contactNames;
    private final UserStatus status;
}