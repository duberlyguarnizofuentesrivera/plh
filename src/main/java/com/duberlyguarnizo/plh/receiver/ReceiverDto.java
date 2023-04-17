package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * A DTO for the {@link Receiver} entity
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class ReceiverDto extends RepresentationModel<ReceiverDto> implements Serializable {
    Long createdBy;
    LocalDateTime createdDate;
    Long lastModifiedBy;
    LocalDateTime lastModifiedDate;
    String notes;
    Long id;
    PersonType type;
    @NotBlank String names;
    String idNumber;
    String phone;
    @Email String email;
    String contactNames;
    Set<Long> addressIds;
}