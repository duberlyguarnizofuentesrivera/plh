package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.address.Address;
import com.duberlyguarnizo.plh.auditing.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Receiver extends AuditableEntity {
    @Id
    @GeneratedValue
    private Long id;
    private boolean company;
    @NotBlank
    private String names;
    @Column(unique = true)
    private String idNumber;
    private String phone;
    @Email
    private String email;
    private String contactNames;
    @ToString.Exclude
    @OneToMany
    private Set<Address> addresses = new HashSet<>();
}
