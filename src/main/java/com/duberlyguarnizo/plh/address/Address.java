package com.duberlyguarnizo.plh.address;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    private String region;
    private String province;
    private String district;
    @NotBlank
    private String addressLine;
    private String observations;
    @CreationTimestamp
    private LocalDateTime creationDate;
    @UpdateTimestamp
    private LocalDateTime modificationDate;
}
