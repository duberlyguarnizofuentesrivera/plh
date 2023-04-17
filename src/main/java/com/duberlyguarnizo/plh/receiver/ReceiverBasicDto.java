package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.enums.PersonType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

/**
 * A DTO for the {@link Receiver} entity
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class ReceiverBasicDto extends RepresentationModel<ReceiverBasicDto> implements Serializable {
    Long id;
    String notes;
    PersonType type;
    @NotBlank String names;
    String idNumber;
    String contactNames;
}