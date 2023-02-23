package com.duberlyguarnizo.plh.address;

import com.duberlyguarnizo.plh.auditing.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Address extends AuditableEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String region;
    private String province;
    private String district;
    @NotBlank
    private String addressLine;
    private String observations;
}
